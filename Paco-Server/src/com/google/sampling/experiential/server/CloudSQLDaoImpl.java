package com.google.sampling.experiential.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTimeZone;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.sampling.experiential.model.Event;
import com.google.sampling.experiential.model.What;
import com.google.sampling.experiential.server.migration.MigrationOutput;
import com.google.sampling.experiential.shared.EventDAO;
import com.google.sampling.experiential.shared.WhatDAO;
import com.pacoapp.paco.shared.model2.EventBaseColumns;
import com.pacoapp.paco.shared.model2.OutputBaseColumns;
import com.pacoapp.paco.shared.util.ErrorMessages;
import com.pacoapp.paco.shared.util.TimeUtil;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

public class CloudSQLDaoImpl implements CloudSQLDao {
  public static final Logger log = Logger.getLogger(CloudSQLDaoImpl.class.getName());
  private static Map<String, Integer> eventsOutputColumns = null;
  private static List<Column> eventColList = Lists.newArrayList();
  private static List<Column> outputColList = Lists.newArrayList();
  public static final String ID = "_id";
  public static final String TRUE = "true";
  private static final String selectOutputsSql = "select * from outputs where event_id =?";

  static {
    eventColList.add(new Column(EventBaseColumns.EXPERIMENT_ID));
    eventColList.add(new Column(EventBaseColumns.EXPERIMENT_NAME));
    eventColList.add(new Column(EventBaseColumns.EXPERIMENT_VERSION));
    eventColList.add(new Column(EventBaseColumns.SCHEDULE_TIME));
    eventColList.add(new Column(EventBaseColumns.RESPONSE_TIME));
    eventColList.add(new Column(EventBaseColumns.GROUP_NAME));
    eventColList.add(new Column(EventBaseColumns.ACTION_TRIGGER_ID));
    eventColList.add(new Column(EventBaseColumns.ACTION_TRIGGER_SPEC_ID));
    eventColList.add(new Column(EventBaseColumns.ACTION_ID));
    eventColList.add(new Column(EventBaseColumns.WHO));
    eventColList.add(new Column("`" + EventBaseColumns.WHEN + "`"));
    eventColList.add(new Column(EventBaseColumns.PACO_VERSION));
    eventColList.add(new Column(EventBaseColumns.APP_ID));
    eventColList.add(new Column(EventBaseColumns.JOINED));
    eventColList.add(new Column(EventBaseColumns.SORT_DATE));
    eventColList.add(new Column(EventBaseColumns.CLIENT_TIME_ZONE));
    eventColList.add(new Column(ID));
    outputColList.add(new Column(OutputBaseColumns.EVENT_ID));
    outputColList.add(new Column(OutputBaseColumns.NAME));
    outputColList.add(new Column(OutputBaseColumns.ANSWER));
    eventsOutputColumns = new HashMap<String, Integer>();
        
    for(int ct = 1; ct <= eventColList.size(); ct ++) {
      eventsOutputColumns.put(eventColList.get(ct-1).getColumnName(), ct);
    }
    for(int ct = 0; ct < outputColList.size(); ct ++) {
      eventsOutputColumns.put(outputColList.get(ct).getColumnName(), eventsOutputColumns.size() + ct);
    }
  }

  @Override
  public boolean insertEvent(Event event) throws SQLException, ParseException {
    if (event == null) {
      log.warning(ErrorMessages.NOT_VALID_DATA.getDescription());
      return false;
    }

    Connection conn = null;
    PreparedStatement statementCreateEvent = null;
    PreparedStatement statementCreateEventOutput = null;
    Date utcWhenTime = null;
    Date utcResponseTime = null;
    Date utcScheduledTime = null;
    boolean retVal = false;
    //startCount for setting paramter index
    int i = 1 ;
    ExpressionList eventExprList = new ExpressionList();
    ExpressionList outputExprList = new ExpressionList();
    List<Expression> exp = Lists.newArrayList();
    List<Expression>  out = Lists.newArrayList();
    Insert eventInsert = new Insert();
    Insert outputInsert = new Insert();
   
    try{
      utcWhenTime = TimeUtil.convertToUTC(event.getWhen(), event.getTimeZone());
      utcResponseTime = TimeUtil.convertToUTC(event.getResponseTime(), event.getTimeZone());
      utcScheduledTime = TimeUtil.convertToUTC(event.getScheduledTime(), event.getTimeZone());
    } catch (ParseException pe){
      log.severe(ErrorMessages.CONVERT_TO_UTC.getDescription()+pe);
      throw pe;
    }

    try {
      log.info("Inserting event->" + event.getId());
      conn = CloudSQLConnectionManager.getInstance().getConnection();
      conn.setAutoCommit(false);
      eventInsert.setTable(new Table(EventBaseColumns.TABLE_NAME));
      outputInsert.setTable(new Table(OutputBaseColumns.TABLE_NAME));
      eventInsert.setUseValues(true);
      outputInsert.setUseValues(true);
      eventExprList.setExpressions(exp);
      outputExprList.setExpressions(out);
      eventInsert.setItemsList(eventExprList);
      outputInsert.setItemsList(outputExprList);
      eventInsert.setColumns(eventColList);
      outputInsert.setColumns(outputColList);
      // Adding ? for prepared stmt
      for (Column c : eventColList) {
        ((ExpressionList) eventInsert.getItemsList()).getExpressions().add(new JdbcParameter());
      }

      for (Column c : outputColList) {
        ((ExpressionList) outputInsert.getItemsList()).getExpressions().add(new JdbcParameter());
      }
 
      statementCreateEvent = conn.prepareStatement(eventInsert.toString());
      statementCreateEvent.setLong(i++, Long.parseLong(event.getExperimentId()));
      statementCreateEvent.setString(i++, event.getExperimentName());
      statementCreateEvent.setInt(i++, event.getExperimentVersion());
      statementCreateEvent.setTimestamp(i++, event.getScheduledTime() != null ? new Timestamp(utcScheduledTime.getTime()): null);
      statementCreateEvent.setTimestamp(i++, event.getResponseTime() != null ? new Timestamp(utcResponseTime.getTime()): null);
      statementCreateEvent.setString(i++, event.getExperimentGroupName());
      statementCreateEvent.setLong(i++, event.getActionTriggerId() != null ? new Long(event.getActionTriggerId()) : java.sql.Types.NULL);
      statementCreateEvent.setLong(i++, event.getActionTriggerSpecId() != null ? new Long(event.getActionTriggerId()) : java.sql.Types.NULL);
      statementCreateEvent.setLong(i++, event.getActionId() != null ? new Long(event.getActionId()) : java.sql.Types.NULL);
      statementCreateEvent.setString(i++, event.getWho());
      statementCreateEvent.setTimestamp(i++, event.getWhen() != null ? new Timestamp(utcWhenTime.getTime()): null);
      statementCreateEvent.setString(i++, event.getPacoVersion());
      statementCreateEvent.setString(i++, event.getAppId());
      statementCreateEvent.setNull(i++, java.sql.Types.BOOLEAN);
      String joinedStat = event.getWhatByKey(EventBaseColumns.JOINED);
      if (joinedStat != null) {
        if (joinedStat.equalsIgnoreCase(TRUE)) {
          statementCreateEvent.setBoolean(i++, true);
        } else {
          statementCreateEvent.setBoolean(i++, false);
        }
      } 
      statementCreateEvent.setTimestamp(i++, event.getResponseTime()!= null ? new Timestamp(utcResponseTime.getTime()): new Timestamp(utcScheduledTime.getTime()));
      statementCreateEvent.setString(i++, event.getTimeZone());
      statementCreateEvent.setLong(i++, event.getId());
      statementCreateEvent.execute();
      
      statementCreateEventOutput = conn.prepareStatement(outputInsert.toString());
      for (String key : event.getWhatKeys()) {
        String whatAnswer = event.getWhatByKey(key);
        statementCreateEventOutput.setLong(1, event.getId());
        statementCreateEventOutput.setString(2, key);
        statementCreateEventOutput.setString(3, whatAnswer);
        statementCreateEventOutput.addBatch();
      }
      statementCreateEventOutput.executeBatch();
      conn.commit();
      retVal = true;
    } finally {
      try {
        if (statementCreateEventOutput != null) {
          statementCreateEventOutput.close();
        }
        if (statementCreateEvent != null) {
          statementCreateEvent.close();
        }
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException ex1) {
        log.warning(ErrorMessages.CLOSING_RESOURCE_EXCEPTION.getDescription() + ex1);
      }
    }
    return retVal;
  }
  
  @Override
  public boolean insertEventsInBatch(List<Event> events) {
    if (events == null) {
      log.warning(ErrorMessages.NOT_VALID_DATA.getDescription());
      return false;
    }
    
    Connection conn = null;
    PreparedStatement statementCreateEvent = null;
    boolean retVal = false;
    //startCount for setting parameter index
    int i = 1 ;
    int eventCtr = 0;
    ExpressionList eventExprList = new ExpressionList();
    List<Expression> exp = Lists.newArrayList();
    Insert eventInsert = new Insert();
    
    try {
      conn = CloudSQLConnectionManager.getInstance().getConnection();
      conn.setAutoCommit(false);
      eventInsert.setTable(new Table(EventServerColumns.TABLE_NAME));
      eventInsert.setUseValues(true);
      eventExprList.setExpressions(exp);
      eventInsert.setItemsList(eventExprList);
      eventInsert.setColumns(eventColList);
      // Adding ? for prepared stmt
      for (Column c : eventColList) {
        ((ExpressionList) eventInsert.getItemsList()).getExpressions().add(new JdbcParameter());
      }
 
      statementCreateEvent = conn.prepareStatement(eventInsert.toString());
      for(eventCtr = 0; eventCtr < events.size(); eventCtr++) {
        Event event = events.get(eventCtr);
        statementCreateEvent.setLong(i++, Long.parseLong(event.getExperimentId()));
        statementCreateEvent.setString(i++, event.getExperimentName());
        statementCreateEvent.setInt(i++, event.getExperimentVersion()!=null ? event.getExperimentVersion() : java.sql.Types.NULL);
        statementCreateEvent.setTimestamp(i++, event.getScheduledTime() != null ? new Timestamp(event.getScheduledTime().getTime()): null);
        statementCreateEvent.setTimestamp(i++, event.getResponseTime() != null ? new Timestamp(event.getResponseTime().getTime()): null);
        statementCreateEvent.setString(i++, event.getExperimentGroupName());
        statementCreateEvent.setLong(i++, event.getActionTriggerId() != null ? new Long(event.getActionTriggerId()) : java.sql.Types.NULL);
        statementCreateEvent.setLong(i++, event.getActionTriggerSpecId() != null ? new Long(event.getActionTriggerId()) : java.sql.Types.NULL);
        statementCreateEvent.setLong(i++, event.getActionId() != null ? new Long(event.getActionId()) : java.sql.Types.NULL);
        statementCreateEvent.setString(i++, event.getWho());
        statementCreateEvent.setTimestamp(i++, event.getWhen() != null ? new Timestamp(event.getWhen().getTime()): null);
        statementCreateEvent.setString(i++, event.getPacoVersion());
        statementCreateEvent.setString(i++, event.getAppId());
        statementCreateEvent.setNull(i++, java.sql.Types.BOOLEAN);
        statementCreateEvent.setTimestamp(i++, event.getResponseTime()!= null ? new Timestamp(event.getResponseTime().getTime()): new Timestamp(event.getScheduledTime().getTime()));
        statementCreateEvent.setString(i++, event.getTimeZone());
        statementCreateEvent.setLong(i++, event.getId());
        statementCreateEvent.setLong(i++, event.getResponseTime() != null ? event.getResponseTime().getTime(): java.sql.Types.NULL);
        if(eventCtr == events.size()-1) {
          log.info("inserting to cloud sql " +  event.getId());
        }
        statementCreateEvent.addBatch();
        i = 1;
      }
      statementCreateEvent.executeBatch();
      conn.commit();
      retVal = true;
    } catch (Exception e) {
      log.info(ErrorMessages.GENERAL_EXCEPTION.getDescription() + e.getCause());
      e.printStackTrace(System.out);
      insertFailedEvent(events.get(eventCtr-1).getId()+" is the last event id in this insert batch", ErrorMessages.EVENT_MIGRATION_EXCEPTION.getDescription(), e.getMessage());
    }
    finally {
      try {
        if (statementCreateEvent != null) {
          statementCreateEvent.close();
        }
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException ex1) {
        log.warning(ErrorMessages.CLOSING_RESOURCE_EXCEPTION.getDescription() + ex1);
      }
    }
    return retVal;
  }
  
  @Override
  public boolean insertOutputsInBatch(Set<MigrationOutput> outputs) {
    if (outputs == null) {
      log.warning(ErrorMessages.NOT_VALID_DATA.getDescription());
      return false;
    }
    
    Connection conn = null;
    PreparedStatement statementCreateOutput = null;
    boolean retVal = false;
    //startCount for setting parameter index
    int i = 1 ;
    ExpressionList outputList = new ExpressionList();
    List<Expression> exp = Lists.newArrayList();
    Insert outputInsert = new Insert();
    
    try {
      conn = CloudSQLConnectionManager.getInstance().getConnection();
      conn.setAutoCommit(false);
      outputInsert.setTable(new Table(OutputBaseColumns.TABLE_NAME));
      outputInsert.setUseValues(true);
      outputList.setExpressions(exp);
      outputInsert.setItemsList(outputList);
      outputInsert.setColumns(outputColList);
      // Adding ? for prepared stmt
      for (Column c : outputColList) {
        ((ExpressionList) outputInsert.getItemsList()).getExpressions().add(new JdbcParameter());
      }
 
      statementCreateOutput = conn.prepareStatement(outputInsert.toString());
      for (MigrationOutput tempOutput : outputs) {
        statementCreateOutput.setLong(i++, tempOutput.getEventId());
        statementCreateOutput.setString(i++, tempOutput.getText());
        statementCreateOutput.setString(i++, tempOutput.getAnswer());
        statementCreateOutput.addBatch();
        i = 1;
      }
      statementCreateOutput.executeBatch();
      conn.commit();
      retVal = true;
    } catch (Exception e) {
      log.info(ErrorMessages.GENERAL_EXCEPTION.getDescription() + e.getCause());
      e.printStackTrace(System.out);
    }
    finally {
      try {
        if (statementCreateOutput != null) {
          statementCreateOutput.close();
        }
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException ex1) {
        log.warning(ErrorMessages.CLOSING_RESOURCE_EXCEPTION.getDescription() + ex1);
      }
    }
    return retVal;
  }

  @Override
  public List<EventDAO> getEvents(String query, DateTimeZone tzForClient) throws SQLException, ParseException {
    List<EventDAO> evtDaoList = Lists.newArrayList();
    EventDAO event = null;
    Connection conn = null;
    Map<Long, EventDAO> eventMap = null;
    PreparedStatement statementSelectEvent = null;
    ResultSet rs = null;

    try {
      conn = CloudSQLConnectionManager.getInstance().getConnection();
      // While we have to retrieve millions of records at a time, we might run
      // into java heap space issue.
      // The following two properties(rs type forward only, concur read only,
      // statement fetch size) of statement are specific to mysql.
      // This signals the driver to stream records one at a time
      statementSelectEvent = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      statementSelectEvent.setFetchSize(Integer.MIN_VALUE);

      Long st1Time = System.currentTimeMillis();
      rs = statementSelectEvent.executeQuery();
      Long st2Time = System.currentTimeMillis();
      
      log.info("step 1 " + query + "took" + (st2Time- st1Time));
      if (rs != null) {
        // to maintain the insertion order
        eventMap = Maps.newLinkedHashMap();
        while (rs.next()) {
          event = createEvent(rs);
          adjustTimeZone(event);
          EventDAO oldEvent = eventMap.get(event.getId());
          if (oldEvent == null) {
            // get all outputs for this event, and add to this event
            event.setWhat(getOutputs(event.getId()));
            eventMap.put(event.getId(), event);
          }
        }
      }
      // TODO step 2 sometimes takes 4 times longer to execute than the query.
      // Not sure why??
      log.info("step 2 took" + (System.currentTimeMillis() - st2Time));
    } finally {
      try {
        if (statementSelectEvent != null) {
          statementSelectEvent.close();
        }
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException ex1) {
        log.warning(ErrorMessages.CLOSING_RESOURCE_EXCEPTION.getDescription()+ ex1);
      }
    }
    if (eventMap != null) {
      evtDaoList = Lists.newArrayList(eventMap.values());
    }

    return evtDaoList;
  }
  
  private List<WhatDAO> getOutputs(Long eventId) throws SQLException {
    List<WhatDAO> whatLst = Lists.newArrayList();
    WhatDAO whatObj = null;
    String question = null;
    String answer = null;
    Connection conn = null;
    PreparedStatement statementSelectOutput = null;
    try {
      conn = CloudSQLConnectionManager.getInstance().getConnection();
      statementSelectOutput = conn.prepareStatement(selectOutputsSql);
      statementSelectOutput.setLong(1, eventId);
      ResultSet rs = statementSelectOutput.executeQuery();
      while(rs.next()){
        question = rs.getString(OutputBaseColumns.NAME);
        answer = rs.getString(OutputBaseColumns.ANSWER);
        whatObj = new WhatDAO(question, answer);
        whatLst.add(whatObj);
      }
    } finally {
      try {
        if (statementSelectOutput != null) {
          statementSelectOutput.close();
        }
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException ex1) {
        log.warning(ErrorMessages.CLOSING_RESOURCE_EXCEPTION.getDescription()+ ex1);
      }
    }
      
    return whatLst;
  }

  private void adjustTimeZone(EventDAO event) throws ParseException {
    String tz = event.getTimezone();
    event.setScheduledTime(TimeUtil.convertToLocal(event.getScheduledTime(), tz));
    event.setResponseTime(TimeUtil.convertToLocal(event.getResponseTime(), tz));
  }

  private EventDAO createEvent(ResultSet rs) {
    EventDAO event = new EventDAO();
    List<WhatDAO> whatList = Lists.newArrayList();
    // setting an empty map for possible outputs. Even if the qry does not ask
    // for output fields, we send an empty output map
    event.setWhat(whatList);
    try {
      ResultSetMetaData rsmd = rs.getMetaData();
      for (int i = 1; i <= rsmd.getColumnCount(); i++) {
        String tempColNameInRS = rsmd.getColumnName(i);
        String colLower = tempColNameInRS.toLowerCase();
        Integer colIndex = eventsOutputColumns.get(colLower);
        if (colIndex != null) {
          switch (colIndex) {
          case 1:
            event.setExperimentId(rs.getLong(i));
            break;
          case 2:
            event.setExperimentName(rs.getString(i));
            break;
          case 3:
            event.setExperimentVersion(rs.getInt(i));
            break;
          case 4:
            event.setScheduledTime(rs.getTimestamp(i));
            break;
          case 5:
            event.setResponseTime(rs.getTimestamp(i));
            break;
          case 6:
            event.setExperimentGroupName(rs.getString(i));
            break;
          case 7:
            event.setActionTriggerId(rs.getLong(i));
            break;
          case 8:
            event.setActionTriggerSpecId(rs.getLong(i));
            break;
          case 9:
            event.setActionId(rs.getLong(i));
            break;
          case 10:
            event.setWho(rs.getString(i));
            break;
          case 11:
            event.setWhen(rs.getTimestamp(i));
            break;
          case 12:
            event.setPaco_version(rs.getString(i));
            break;
          case 13:
            event.setAppId(rs.getString(i));
            break;
          case 14:
            event.setJoined(rs.getBoolean(i));
            break;
          case 15:
            event.setSortDate(rs.getTimestamp(i));
            break;
          case 16:
            event.setTimezone(rs.getString(i));
            break;
          case 17:
          case 18:
            event.setId(rs.getLong(i));
            break;
          case 19:
            List<WhatDAO> whTextLst = event.getWhat();
            whTextLst.add(new WhatDAO(OutputBaseColumns.NAME, rs.getString(i)));
            break;
          case 20:
            List<WhatDAO> whAnsLst = event.getWhat();
            whAnsLst.add(new WhatDAO(OutputBaseColumns.ANSWER, rs.getString(i)));
            break;
          }
        }
      }
    } catch (SQLException ex) {
      log.warning(ErrorMessages.SQL_EXCEPTION.getDescription() + ex);
    }
    return event;
  }

  @Override
  public String createTables() throws SQLException {
    String retString = null;
    Connection conn = null;
    PreparedStatement statementCreateEvent = null;
    PreparedStatement statementCreateOutput = null;

    try {

      conn = CloudSQLConnectionManager.getInstance().getConnection();
      // TODO Sub Partition size for the experiment hash bucket
      final String createEventsTableSql = "CREATE TABLE `" + EventBaseColumns.TABLE_NAME + "` (" +

                                          "`" + ID + "` bigint(20) NOT NULL," + "`" + EventBaseColumns.EXPERIMENT_ID
                                          + "` bigint(20) NOT NULL," + "`" + EventBaseColumns.EXPERIMENT_NAME
                                          + "` varchar(45) DEFAULT NULL," + "`" + EventBaseColumns.EXPERIMENT_VERSION
                                          + "` int(11) DEFAULT NULL," + "`" + EventBaseColumns.SCHEDULE_TIME
                                          + "` datetime DEFAULT NULL," + "`" + EventBaseColumns.RESPONSE_TIME
                                          + "` datetime DEFAULT NULL," + "`" + EventBaseColumns.GROUP_NAME
                                          + "` varchar(45) DEFAULT NULL," + "`" + EventBaseColumns.ACTION_ID
                                          + "` bigint(20) DEFAULT NULL," + "`" + EventBaseColumns.ACTION_TRIGGER_ID
                                          + "` bigint(20) DEFAULT NULL," + "`" + EventBaseColumns.ACTION_TRIGGER_SPEC_ID
                                          + "` bigint(20) DEFAULT NULL," + "`" + EventBaseColumns.WHO
                                          + "` varchar(45) NOT NULL," + "`" + EventBaseColumns.PACO_VERSION
                                          + "` varchar(45) DEFAULT NULL," + "`" + EventBaseColumns.APP_ID
                                          + "` varchar(45) DEFAULT NULL," + "`" + EventBaseColumns.WHEN
                                          + "` datetime DEFAULT NULL," + "`" + EventBaseColumns.ARCHIVE_FLAG
                                          + "` tinyint(4) NOT NULL DEFAULT '0'," + "`" + EventBaseColumns.JOINED
                                          + "` tinyint(1)  DEFAULT NULL," + "`" + EventBaseColumns.SORT_DATE
                                          + "` datetime  DEFAULT NULL," + "`" + EventBaseColumns.CLIENT_TIME_ZONE
                                          + "` varchar(20) DEFAULT NULL," + "PRIMARY KEY (`" + ID + "`),"
                                          + "KEY `when_index` (`" + EventBaseColumns.WHEN + "`),"
                                          + "KEY `exp_id_resp_time_index` (`" + EventBaseColumns.EXPERIMENT_ID + "`,`"
                                          + EventBaseColumns.RESPONSE_TIME + "`)," + "KEY `exp_id_when_index` (`"
                                          + EventBaseColumns.EXPERIMENT_ID + "`,`" + EventBaseColumns.WHEN + "`),"
                                          + "KEY `exp_id_who_when_index` (`" + EventBaseColumns.EXPERIMENT_ID + "`,`"
                                          + EventBaseColumns.WHO + "`,`" + EventBaseColumns.WHEN + "`)"
                                          + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";

      final String createOutputsTableSql = "CREATE TABLE `" + OutputBaseColumns.TABLE_NAME+ "` (" + "`" 
                                           + OutputBaseColumns.EVENT_ID + "` bigint(20) NOT NULL," + "`"
                                           + OutputBaseColumns.NAME + "` varchar(500) NOT NULL," + "`"
                                           + OutputBaseColumns.ANSWER + "` varchar(500) DEFAULT NULL," + "`"
                                           + OutputBaseColumns.ARCHIVE_FLAG + "` tinyint(4) NOT NULL DEFAULT '0',"
                                           + "PRIMARY KEY (`" + OutputBaseColumns.EVENT_ID + "`,`"
                                           + OutputBaseColumns.NAME + "`)," + "KEY `event_id_index` (`"
                                           + OutputBaseColumns.EVENT_ID + "`)," + "KEY `text_index` (`"
                                           + OutputBaseColumns.NAME + "`)" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
      
      final String createFailedEventsTableSql = "CREATE TABLE `" +  FailedEventServerColumns.TABLE_NAME +  "` (" + "`" 
                                            + FailedEventServerColumns.ID + "` bigint(20) NOT NULL AUTO_INCREMENT," + "`"
                                            + FailedEventServerColumns.EVENT_JSON + "` varchar(3000) NOT NULL," + "`"
                                            + FailedEventServerColumns.FAILED_INSERT_TIME + "` datetime  DEFAULT CURRENT_TIMESTAMP," + "`"
                                            + FailedEventServerColumns.REASON + "` varchar(500) DEFAULT NULL," + "`"
                                            + FailedEventServerColumns.COMMENTS + "` varchar(1000) DEFAULT NULL,"
                                            + "PRIMARY KEY (`" + FailedEventServerColumns.ID + "`)"+") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1";
     
      statementCreateEvent = conn.prepareStatement(createEventsTableSql);

      statementCreateEvent.execute();
      log.info("created events");
      // TODO better handling
      retString = "created events table. ";
      statementCreateOutput = conn.prepareStatement(createOutputsTableSql);

      statementCreateOutput.execute();
      log.info("created outputs");
      // TODO better handling
      retString = retString + "Created outputs table";
    } finally {
      try {
        if (statementCreateEvent != null) {
          statementCreateEvent.close();
        }
        if (statementCreateOutput != null) {
          statementCreateOutput.close();
        }
        if (conn != null) {
          conn.close();
        }

      } catch (SQLException ex1) {
        log.warning(ErrorMessages.CLOSING_RESOURCE_EXCEPTION.getDescription() + ex1);
      }
    }
    return retString;
  }
}
