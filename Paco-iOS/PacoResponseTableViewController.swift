//
//  PacoResponseTableViewController.swift
//  Paco
//
//  Created by northropo on 11/5/15.
//  Copyright (c) 2015 Paco. All rights reserved.
//

import UIKit
import Foundation
 

class PacoResponseTableViewController: UITableViewController,PacoInputTable {
    
      var selectedIndexPath :NSIndexPath?
      var validationOn:Bool?
      var  inputs:JavaUtilList!
    
      var    selectedString:String!
    
      var   cells = [Int:PacoTableViewExpandingCellBase]()
      var   inputValues = [Int:String]()
    
    
      let cellID         = "cellDate"
      let cellSelectId   = "cellSelect"
      let cellText       = "cellText"
      let cellMC   = "cellMultipleChoice"
      let cellLikert   = "cellLikeArt"
    
    
    enum InputType {
        case OpenText
        case SingleSelect
        case MultipleSelect
        case Photo
        case Location
        case Likert
        case LikertSmiley
        case Unknown
    }
    
    
    
     func textChanged(row:Int,text:String)
     {
        inputValues[row] = text;
     }
    
    
    
    func selected(row: Int, selected: String)
    {
        inputValues[row] = selected;
    }
    
    init!(nibName nibNameOrNil: String!, bundle nibBundleOrNil: NSBundle!, input userform:JavaUtilList )
    {
        
        self.inputs = userform
        super.init(nibName: nibNameOrNil , bundle: nibBundleOrNil)
    
    }
    

    required init!(coder aDecoder: NSCoder!)
    {
         super.init(coder:aDecoder)
 
    }
 
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        var backBtn = UIBarButtonItem(title: "Save", style: UIBarButtonItemStyle.Plain, target: self, action: "btnBack:")
        
        
        var cancelBtn = UIBarButtonItem(title: "Cancel", style: UIBarButtonItemStyle.Plain, target: self, action: "btnCancel:")
 
        navigationItem.leftBarButtonItem  = backBtn
        navigationItem.rightBarButtonItem = cancelBtn
        
        
       // self.navigationController!.navigationItem.leftBarButtonItem = backBtn
        
        
        self.tableView.registerNib(UINib(nibName:"PacoPickerTableViewCell", bundle: nil), forCellReuseIdentifier:self.cellID)
        self.tableView.registerNib(UINib(nibName:"PacoStringSelectorTableViewCell", bundle: nil), forCellReuseIdentifier:self.cellSelectId)
        self.tableView.registerNib(UINib(nibName:"PacoTextTableViewCell", bundle: nil), forCellReuseIdentifier:self.cellText)

        self.tableView.registerNib(UINib(nibName:"PacoMultipleChoiceCellTableViewCell", bundle: nil), forCellReuseIdentifier:self.cellMC)
        
        self.tableView.registerNib(UINib(nibName:"PacoLikertCell", bundle: nil), forCellReuseIdentifier:self.cellLikert)

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    func registerTableCells()
    {
 
     
    }

    
    
    
    @IBAction func btnBack(btn:AnyObject)
    {

        
        
       self.navigationController?.popViewControllerAnimated(true)

    }
    
    
    @IBAction func btnCancel(btn:AnyObject)
    {
        
        
        for(index,cell) in cells {
            
            
            var isValid:Bool =  cell.isValid()
            var ouput:PacoOutput = cell.getResuts()
            validationOn = false
            if(isValid == false)
            {
                validationOn = true
                self.tableView.reloadData()
                break
            }
       
            
         
            println("   \(index)   -- \(cell)  ")
            
            
            
        }
        
       // self.navigationController?.popViewControllerAnimated(true)
        
    }

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        var count  = inputs.size() as Int32
        var  number2 = Int(count)
        return  number2
      
    }
    
    
    func getInputType(input:PAInput2) -> InputType
    {
        var  inputTypeResponse:InputType
        switch input.getResponseType() {
            
        case "list":
            
        var isMultiSelect  = input.valueForKey("multiselect_") as! JavaLangBoolean
        if(isMultiSelect.booleanValue())
        {
            inputTypeResponse = InputType.MultipleSelect
        }
        else
        {
             inputTypeResponse = InputType.SingleSelect
            
        }
            
        case "open text": inputTypeResponse = InputType.OpenText
        case "photo": inputTypeResponse = InputType.Photo
        case "location": inputTypeResponse = InputType.Location
        case "likert": inputTypeResponse = InputType.Likert
        default: inputTypeResponse = InputType.Unknown
            
        }
        
        return inputTypeResponse
        
        
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        var  index = Int32(indexPath.row)
        var input:PAInput2 =  inputs.getWithInt(index) as! PAInput2
        
        var steps:JavaLangInteger   =   input.valueForKeyEx("likertSteps") as!  JavaLangInteger
        var numSteps = steps.integerValue
        
        
         var    cell:PacoTableViewExpandingCellBase?
         var t = getInputType(input)
        
            switch t
            {
                
            case InputType.OpenText:
                
                let   c:PacoTextTableViewCell
                c  = tableView.dequeueReusableCellWithIdentifier(self.cellText, forIndexPath: indexPath) as! PacoTextTableViewCell
                c.inputLabel.text  = input.getName()
                c.inputTexInput.text  = inputValues[indexPath.row]
                c.indexPath = indexPath
                c.parent = self
                cell = c
                
                
            case InputType.MultipleSelect:
                let c:PacoMultipleChoiceCellTableViewCell;
                  c = tableView.dequeueReusableCellWithIdentifier(self.cellMC, forIndexPath: indexPath) as! PacoMultipleChoiceCellTableViewCell
                   c.listChoices = input.getListChoices()
                   c.input = input;
                c.selectLabel.text = input.getName()
                c.reloadTable(input.getListChoices(),singleSelect:false)
              
                cell = c
            case InputType.SingleSelect:
                
                let   c:PacoMultipleChoiceCellTableViewCell
                c = tableView.dequeueReusableCellWithIdentifier(self.cellMC, forIndexPath: indexPath) as! PacoMultipleChoiceCellTableViewCell
                c.selectLabel.text = input.getName()
                c.listChoices = input.getListChoices()
                c.input = input;
           
                c.reloadTable(input.getListChoices(),singleSelect:true)
                
                
                cell = c
                
                
            case InputType.Likert:
                
                let   c:PacoLikertCell
                
                c = tableView.dequeueReusableCellWithIdentifier(self.cellLikert, forIndexPath: indexPath) as! PacoLikertCell
                
                c.likertLabel.text =  input.getName()
                c.input = input;
                 c.numberOfCheckmarks = numSteps
                c.arraingeLabels()
                cell = c
                
            default:
                
                let   c:PacoTextTableViewCell
                c  = tableView.dequeueReusableCellWithIdentifier(self.cellText, forIndexPath: indexPath) as! PacoTextTableViewCell
                c.titleLabel.text = "Unknown type"
             
                cell = c
                
            }
      
            if( validationOn == true )
            {
                 cell!.displayValidationIndicator()
             }
        
            cell!.input = input
            
             cells[indexPath.row] = cell;
        
        return cell!
      
        
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let previousIndexPath = selectedIndexPath
        if indexPath == selectedIndexPath {
            selectedIndexPath = nil
        } else {
            selectedIndexPath = indexPath
        }
        
        var indexPaths : Array<NSIndexPath> = []
        if let previous = previousIndexPath {
            indexPaths += [previous]
        }
        if let current = selectedIndexPath {
            indexPaths += [current]
        }
        if indexPaths.count > 0 {
            tableView.reloadRowsAtIndexPaths(indexPaths, withRowAnimation: UITableViewRowAnimation.Automatic)
        }
    }
    
    override func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        (cell as! PacoTableViewExpandingCellBase).watchFrameChanges()
    }
    
    override func tableView(tableView: UITableView, didEndDisplayingCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        (cell as! PacoTableViewExpandingCellBase).ignoreFrameChanges()
    }
    
    override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(animated)
        
        for cell  in tableView.visibleCells()
        {
            
            if cell is PacoTableViewExpandingCellBase
            {
                 cell.ignoreFrameChanges()
            }
        }
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        
        var  index = Int32(indexPath.row)
        var input:PAInput2 =  inputs.getWithInt(index) as! PAInput2
        var height:CGFloat
        var t = getInputType(input)
        
        switch t
        {
            
        case InputType.OpenText:
            
 
             height = 200
            
        case InputType.MultipleSelect:
            
            input.getListChoices().size();
            var count  = inputs.size() as Int32
            height  = CGFloat(count*55  ) + 100
            
   
        case InputType.SingleSelect:

            input.getListChoices().size();
            var count  = inputs.size() as Int32
            height  = CGFloat(count*55 ) + 100
   
            
         case InputType.Likert:
            height = 80
            
         default:
            height = 40

            
        }
        
 
 
        return height
      }
   
   
    
    
}
