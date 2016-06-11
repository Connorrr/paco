//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: joda-time/src/main/java/org/joda/time/format/DateTimeParserInternalParser.java
//

#include "IOSClass.h"
#include "J2ObjC_source.h"
#include "java/lang/CharSequence.h"
#include "org/joda/time/format/DateTimeParser.h"
#include "org/joda/time/format/DateTimeParserBucket.h"
#include "org/joda/time/format/DateTimeParserInternalParser.h"
#include "org/joda/time/format/InternalParser.h"
#include "org/joda/time/format/InternalParserDateTimeParser.h"

@interface OrgJodaTimeFormatDateTimeParserInternalParser () {
 @public
  id<OrgJodaTimeFormatDateTimeParser> underlying_;
}

- (instancetype)initWithOrgJodaTimeFormatDateTimeParser:(id<OrgJodaTimeFormatDateTimeParser>)underlying;

@end

J2OBJC_FIELD_SETTER(OrgJodaTimeFormatDateTimeParserInternalParser, underlying_, id<OrgJodaTimeFormatDateTimeParser>)

__attribute__((unused)) static void OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(OrgJodaTimeFormatDateTimeParserInternalParser *self, id<OrgJodaTimeFormatDateTimeParser> underlying);

__attribute__((unused)) static OrgJodaTimeFormatDateTimeParserInternalParser *new_OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(id<OrgJodaTimeFormatDateTimeParser> underlying) NS_RETURNS_RETAINED;

__attribute__((unused)) static OrgJodaTimeFormatDateTimeParserInternalParser *create_OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(id<OrgJodaTimeFormatDateTimeParser> underlying);

@implementation OrgJodaTimeFormatDateTimeParserInternalParser

+ (id<OrgJodaTimeFormatInternalParser>)ofWithOrgJodaTimeFormatDateTimeParser:(id<OrgJodaTimeFormatDateTimeParser>)underlying {
  return OrgJodaTimeFormatDateTimeParserInternalParser_ofWithOrgJodaTimeFormatDateTimeParser_(underlying);
}

- (instancetype)initWithOrgJodaTimeFormatDateTimeParser:(id<OrgJodaTimeFormatDateTimeParser>)underlying {
  OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(self, underlying);
  return self;
}

- (id<OrgJodaTimeFormatDateTimeParser>)getUnderlying {
  return underlying_;
}

- (jint)estimateParsedLength {
  return [((id<OrgJodaTimeFormatDateTimeParser>) nil_chk(underlying_)) estimateParsedLength];
}

- (jint)parseIntoWithOrgJodaTimeFormatDateTimeParserBucket:(OrgJodaTimeFormatDateTimeParserBucket *)bucket
                                  withJavaLangCharSequence:(id<JavaLangCharSequence>)text
                                                   withInt:(jint)position {
  return [((id<OrgJodaTimeFormatDateTimeParser>) nil_chk(underlying_)) parseIntoWithOrgJodaTimeFormatDateTimeParserBucket:bucket withNSString:[((id<JavaLangCharSequence>) nil_chk(text)) description] withInt:position];
}

- (void)dealloc {
  RELEASE_(underlying_);
  [super dealloc];
}

+ (const J2ObjcClassInfo *)__metadata {
  static const J2ObjcMethodInfo methods[] = {
    { "ofWithOrgJodaTimeFormatDateTimeParser:", "of", "Lorg.joda.time.format.InternalParser;", 0x8, NULL, NULL },
    { "initWithOrgJodaTimeFormatDateTimeParser:", "DateTimeParserInternalParser", NULL, 0x2, NULL, NULL },
    { "getUnderlying", NULL, "Lorg.joda.time.format.DateTimeParser;", 0x0, NULL, NULL },
    { "estimateParsedLength", NULL, "I", 0x1, NULL, NULL },
    { "parseIntoWithOrgJodaTimeFormatDateTimeParserBucket:withJavaLangCharSequence:withInt:", "parseInto", "I", 0x1, NULL, NULL },
  };
  static const J2ObjcFieldInfo fields[] = {
    { "underlying_", NULL, 0x12, "Lorg.joda.time.format.DateTimeParser;", NULL, NULL, .constantValue.asLong = 0 },
  };
  static const J2ObjcClassInfo _OrgJodaTimeFormatDateTimeParserInternalParser = { 2, "DateTimeParserInternalParser", "org.joda.time.format", NULL, 0x0, 5, methods, 1, fields, 0, NULL, 0, NULL, NULL, NULL };
  return &_OrgJodaTimeFormatDateTimeParserInternalParser;
}

@end

id<OrgJodaTimeFormatInternalParser> OrgJodaTimeFormatDateTimeParserInternalParser_ofWithOrgJodaTimeFormatDateTimeParser_(id<OrgJodaTimeFormatDateTimeParser> underlying) {
  OrgJodaTimeFormatDateTimeParserInternalParser_initialize();
  if ([underlying isKindOfClass:[OrgJodaTimeFormatInternalParserDateTimeParser class]]) {
    return (id<OrgJodaTimeFormatInternalParser>) cast_check(underlying, OrgJodaTimeFormatInternalParser_class_());
  }
  if (underlying == nil) {
    return nil;
  }
  return create_OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(underlying);
}

void OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(OrgJodaTimeFormatDateTimeParserInternalParser *self, id<OrgJodaTimeFormatDateTimeParser> underlying) {
  NSObject_init(self);
  JreStrongAssign(&self->underlying_, underlying);
}

OrgJodaTimeFormatDateTimeParserInternalParser *new_OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(id<OrgJodaTimeFormatDateTimeParser> underlying) {
  OrgJodaTimeFormatDateTimeParserInternalParser *self = [OrgJodaTimeFormatDateTimeParserInternalParser alloc];
  OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(self, underlying);
  return self;
}

OrgJodaTimeFormatDateTimeParserInternalParser *create_OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(id<OrgJodaTimeFormatDateTimeParser> underlying) {
  OrgJodaTimeFormatDateTimeParserInternalParser *self = [[OrgJodaTimeFormatDateTimeParserInternalParser alloc] autorelease];
  OrgJodaTimeFormatDateTimeParserInternalParser_initWithOrgJodaTimeFormatDateTimeParser_(self, underlying);
  return self;
}

J2OBJC_CLASS_TYPE_LITERAL_SOURCE(OrgJodaTimeFormatDateTimeParserInternalParser)