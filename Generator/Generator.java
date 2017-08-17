import java.util.*;
import java.io.File;

public class Generator{

  private ArrayList<PDSTree> Docs = new ArrayList<PDSTree>();
  private ArrayList<PDSTree> AddDocs = new ArrayList<PDSTree>();
  private ArrayList<PDSTree> ChangeDocs = new ArrayList<PDSTree>();
  private ArrayList<PDSTree> DeleteDocs = new ArrayList<PDSTree>();

  private ArrayList<String> Checkpoints = new ArrayList<String>();

  public void GenerateAwards(PDSTree Master){
    AwardTrim(Master);

    int oldTrees = 0;
    Initialize(Master, Docs);
    for (int i = 0; i < 3; i++){
      for (int j = 0; j < Docs.size(); j++){
        Master.gotoRoot();
        Docs.get(j).gotoRoot();
        FindCheckpoints(Master, Docs.get(j));
        }
      oldTrees = Docs.size();
      Checkpoints.clear();
      for (int j = 0; j < Docs.size(); j++){
        GetChoices(Docs.get(j));
      }
      if (i < 2){
        for (int j = 0; j < oldTrees; j++){
          Docs.remove(0);
        }
      }
    }
    for(int i = 0; i < Docs.size(); i++){
      Docs.get(i).gotoRoot();
      MakeData(Docs.get(i), 0);
      Finalize(Docs.get(i));
    }

    Writer writer = new Writer();
    for(int i = 0; i < Docs.size(); i++){
      writer.WriteTree(Docs.get(i), "../GeneratedFiles/ProcurementDocment"+i+"-0.xml");
    }
  }

  public void GenerateMods(PDSTree Master){
    ModTrim(Master);
    ModTrim(Master);

    for(int i = 0; i < Docs.size() ; i++){
      Master.gotoRoot();
      GenAddDel(Master, Docs.get(i));
      GenChange(Master, Docs.get(i));
    }
    //System.out.println(Docs.size());
    Writer writer = new Writer();
    for(int i = 0; i < Docs.size()*3; i++){
      if(i%3 == 0){
        writer.WriteTree(AddDocs.get(i/3), "../GeneratedFiles/ProcurementDocment"+i/3+"-1.xml");
      }else if(i%3 == 1){
        writer.WriteTree(ChangeDocs.get(i/3), "../GeneratedFiles/ProcurementDocment"+i/3+"-2.xml");
      }else if(i%3 == 2){
        writer.WriteTree(DeleteDocs.get(i/3), "../GeneratedFiles/ProcurementDocment"+i/3+"-3.xml");
      }
    }
  }


  private void AwardTrim(PDSTree myTree){
  	if (myTree.containsAttributeKey("name")){
  	  if (myTree.getAttribute("name").equals("AwardModificationInstrument") || myTree.getAttribute("name").equals("UniformProcurementInstrumentIdentifier")){
  	    myTree.deleteNode();
  	  }
  	}
  	for (int i = 0; i < myTree.getChildrenSize(); i ++){
  	  myTree.next(i);
  	  AwardTrim(myTree);
  	  myTree.previous();
  	}
  }

  private void ModTrim(PDSTree myTree){
    if (myTree.containsAttributeKey("name")){
      if (myTree.getAttribute("name").equals("AwardInstrument") || myTree.getAttribute("name").equals("MultipleLineItemsInformation") || myTree.getAttribute("name").equals("UniformProcurementInstrumentIdentifier")){
        myTree.deleteNode();
      }
    }
    for (int i = 0; i < myTree.getChildrenSize(); i ++){
      myTree.next(i);
      ModTrim(myTree);
      myTree.previous();
    }
  }

  private void Initialize(PDSTree Master, ArrayList<PDSTree> Docs){
    Docs.add(new PDSTree());
    CopyToChoices(Master, Docs.get(0));
    Docs.get(0).gotoRoot();
    GetChoices(Docs.get(0));
    Docs.remove(0);
  }

  private void CopyToChoices(PDSTree Master, PDSTree myTree){
    if (!Master.getType().equals("xs:choice")){
      myTree.addChild(Master.getCurrentNode().cloneOrphan());
      if (myTree.getChildrenSize() != 0){
        myTree.next(myTree.getChildrenSize()-1);
      }
      for (int i = 0; i < Master.getChildrenSize(); i++){
        Master.next(i);
        CopyToChoices(Master, myTree);
        Master.previous();
      }
      myTree.previous();
    }else{
      myTree.addChild(Master.getCurrentNode().cloneOrphan());
      myTree.next(myTree.getChildrenSize()-1);
      for (int i = 0; i < Master.getChildrenSize(); i++){
        Master.next(i);
        myTree.addChild(Master.getCurrentNode().cloneOrphan());
        Master.previous();
      }
      myTree.previous();
    }
  }

  private void GetChoices(PDSTree Doc){
    if (!Doc.getType().equals("xs:choice")){
      for (int i = 0; i < Doc.getChildrenSize(); i++){
        Doc.next(i);
        GetChoices(Doc);
        Doc.previous();
      }
    }else{
      for (int i = 0; i < Doc.getChildrenSize(); i++){
        Docs.add(Doc.CloneFullTree());
        MakeChoice(Docs.get(Docs.size()-1), Doc.getData(), i);
      }
    }
  }

  private void MakeChoice(PDSTree myTree, String id, int choice){
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      if (myTree.getChild(i).getType().equals("xs:choice")){
        if (myTree.getChild(i).getData().equals(id)){
          if (!Checkpoints.contains(myTree.getChild(i).getChild(choice).getAttribute("name"))){
            Checkpoints.add(myTree.getChild(i).getChild(choice).getAttribute("name"));
          }
          myTree.getChild(i).getChild(choice).setParent(myTree.getCurrentNode());
          myTree.addChild(myTree.getChild(i).getChild(choice));
          myTree.deleteChild(i);
        }else{
          Random rand = new Random();
          int randint = rand.nextInt(myTree.getChild(i).getChildrenSize());
          myTree.getChild(i).getChild(randint).setParent(myTree.getCurrentNode());
          myTree.addChild(myTree.getChild(i).getChild(randint));
          myTree.deleteChild(i);
        }
      }
    }
    for (int i = 0; i < myTree.getChildrenSize(); i++){
      myTree.next(i);
      MakeChoice(myTree, id, choice);
      myTree.previous();
    }
  }

  private void FindCheckpoints(PDSTree Master, PDSTree myTree){
    for(int i = 0; i < Checkpoints.size(); i++){
      if (Master.getNodeByAttrNameAll(Checkpoints.get(i)) && myTree.getNodeByAttrNameAll(Checkpoints.get(i))){
        //System.out.println("found " + Checkpoints.get(i));
        myTree.previous();
        for (int j = 0; j < myTree.getChildrenSize(); j++){
          if (myTree.getChild(j).containsAttributeKey("name")){
            if (myTree.getChild(j).getAttribute("name").equals(Checkpoints.get(i))){
              myTree.deleteChild(j);
            }
          }
        }
        CopyToChoices(Master, myTree);
      }else{
        //System.out.println("could not find " + Checkpoints.get(i));
      }
    }
  }

  private void MakeData(PDSTree myTree, int base){
    if(myTree.getChildrenSize() == 0){
      CollapseData(myTree.getCurrentNode(), base);
    }
    for(int i = 0; i < myTree.getChildrenSize(); i ++){
      myTree.next(i);
      MakeData(myTree, base);
      myTree.previous();
    }
  }

  private void CollapseData(PDSNode myNode, int base){
    Random rand = new Random();
    if(myNode.getData() == ""){
      if(myNode.getRestrictionType() != null){
        if (myNode.getRestrictionType().equals("string")){
          myNode.setData(HandleString(myNode, base));
        }else if(myNode.getRestrictionType().equals("token")){
          if(myNode.getAttribute("name").equals("MilitaryOrFederalOverseas")){
            myNode.setData(myNode.getRestriction("enumeration5"));
          }else if(myNode.getAttribute("name").equals("ProcurementInstrumentDescription")){
            myNode.setData("Represented Contract");
          }else{
            myNode.setData(myNode.getRestriction("enumeration" + rand.nextInt(myNode.getRestrictionSize())));
          }
        }else if(myNode.getRestrictionType().equals("date")){
          myNode.setData("20" + (rand.nextInt(40)+10) + "-" + (rand.nextInt(9)+1) + "-" + (rand.nextInt(19)+10));
        }else if(myNode.getRestrictionType().equals("gYearMonth")){
          myNode.setData("3017-" + (rand.nextInt(9)+1));
        }else if(myNode.getRestrictionType().equals("positiveInteger")){
          myNode.setData("" +rand.nextInt(10)+1);
        }else if(myNode.getRestrictionType().equals("base64Binary")){
          myNode.setData("BinaryTokenHere");
        }else if(myNode.getRestrictionType().equals("nonNegativeInteger")){
          myNode.setData("" +rand.nextInt(10));
        }else if(myNode.getRestrictionType().equals("time")){
          myNode.setData("TimeHere");
        }else if(myNode.getRestrictionType().equals("decimal")){
          myNode.setData("" + (rand.nextInt(3)+1) +"."+ (rand.nextInt(9)+1));
        }else if(myNode.getRestrictionType().equals("float")){
          myNode.setData("" +(rand.nextInt(10)+1));
        }
      }else{
        if(base == 0){
          myNode.setData(myNode.getAttribute("name") + "_base_data_here");
        }else if(base == 1){
          myNode.setData(myNode.getAttribute("name") + "_added_data_here");
        }else if(base == 2){
          myNode.setData(myNode.getAttribute("name") + "_changed_data_here");
        }
      }
    }
  }

  private String HandleString(PDSNode myNode, int base){
    String returnVal = "";
    if(!myNode.getAttribute("name").equals("NonDoDNumber")){
      if(myNode.getRestrictionSize() > 0){
        for(int i = 0; i < myNode.getRestrictionSize(); i++){
          if(myNode.containsRestrictionKey("pattern"+i)){
            String pattern =  myNode.getRestriction("pattern"+i);
            returnVal = ExpressPattern(pattern);
          }
        }
      }else{
        if(base == 0){
          returnVal = myNode.getAttribute("name") + "_base_data_here";
        }else if(base == 1){
          returnVal = myNode.getAttribute("name") + "_added_data_here";
        }else if(base == 2){
          returnVal = myNode.getAttribute("name") + "_changed_data_here";
        }
      }
    }else{
      Random rand = new Random();
      int length = rand.nextInt(Integer.parseInt(myNode.getRestriction("maxLength1"))) + Integer.parseInt(myNode.getRestriction("minLength0"));
      String temp = "";
      for(int i = 0; i < length; i++){
        temp += Integer.toString(rand.nextInt(9));
      }
      returnVal = temp;
    }
    return(returnVal);
  }

  private String ExpressPattern(String pattern){
    String returnVal = "";
    String[] rules = pattern.split("[(]");
    for (int i = 0; i < rules.length; i++){
      if(i > 0){
        String template = rules[i].split("[)]")[0];
        String amountStr = rules[i].split("[)]")[1];
        int amountInt;

        if(amountStr.equals("*")){amountInt = 3;}
        else{amountInt = Integer.parseInt(amountStr.substring(1, amountStr.length() -1));}

        char[] temp = template.substring(1, template.length()-1).toCharArray();
        ArrayList<Character> Choices = new ArrayList<Character>();
        for(int j = 0; j < temp.length; j++){
          if(temp[j] == '-'){
            for(int k = (int)temp[j-1]+1; k < (int)temp[j+1]; k++){
              Choices.add((char)k);
            }
          }else{
            Choices.add(temp[j]);
          }
        }

        for(int j = 0; j < amountInt; j++){
          Random rand = new Random();
          returnVal += Choices.get(rand.nextInt(Choices.size()));
        }
      }
    }

    return(returnVal);
  }

  private void GenAddDel(PDSTree ModMaster, PDSTree myTree){
    //Copy the data that matters from the base tree
    PDSTree addTree = new PDSTree(myTree.getRoot().cloneOrphan());
    for(int i = 0; i < myTree.getChildrenSize()-1; i++){
      myTree.next(i);
      addTree.addChild(myTree.CloneSubTree());
      myTree.previous();
    }
    ModMaster.getNodeByAttrNameAll("AwardModificationInstrument");
    addTree.addChild(ModMaster.CloneSubTree());
    myTree.getNodeByAttrNameAll("ContractLineItems");
    addTree.getNodeByAttrNameAll("ContractLineItems");

    if(myTree.getChild(0).getAttribute("name").equals("LineItems")){
      addTree.getChild(0).getChild(0).setParent(addTree.getCurrentNode());
      addTree.addChild(addTree.getChild(0).getChild(0));
      addTree.deleteChild(0);
    }else{
      addTree.getChild(0).getChild(1).setParent(addTree.getCurrentNode());
      addTree.addChild(addTree.getChild(0).getChild(1));
      addTree.deleteChild(0);
    }

    PDSTree deleteTree = addTree.CloneFullTree();
    FillLineItems(addTree, deleteTree);
    // RemoveHeader4Now(addTree);
    // RemoveHeader4Now(deleteTree);
    FillHeaderAddDel(addTree, deleteTree, myTree);

    AddDocs.add(addTree);
    DeleteDocs.add(deleteTree);
  }

  private void GenChange(PDSTree ModMaster, PDSTree myTree){
    myTree.gotoRoot();
    PDSTree changeTree = new PDSTree(myTree.getRoot().cloneOrphan());
    for(int i = 0; i < myTree.getChildrenSize()-1; i++){
      myTree.next(i);
      changeTree.addChild(myTree.CloneSubTree());
      myTree.previous();
    }
    ModMaster.getNodeByAttrNameAll("AwardModificationInstrument");
    changeTree.addChild(ModMaster.CloneSubTree());
    myTree.getNodeByAttrNameAll("ContractLineItems");
    changeTree.getNodeByAttrNameAll("ContractLineItems");

    if(myTree.getChild(0).getAttribute("name").equals("LineItems")){
      changeTree.getChild(0).getChild(1).setParent(changeTree.getCurrentNode());
      changeTree.addChild(changeTree.getChild(0).getChild(1));
      changeTree.deleteChild(0);
    }else{
      changeTree.getChild(0).getChild(0).setParent(changeTree.getCurrentNode());
      changeTree.addChild(changeTree.getChild(0).getChild(0));
      changeTree.deleteChild(0);
    }

    changeTree.next(0);
    changeTree.deleteChild(1);
    changeTree.deleteChild(1);
    changeTree.next(0);

    myTree.next(0);
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      myTree.next(i);
      changeTree.next(1);
      changeTree.addChild(myTree.CloneSubTree());
      changeTree.deleteChild(0);
      changeTree.previous();
      changeTree.next(2);
      changeTree.addChild(myTree.CloneSubTree());
      changeTree.deleteChild(0);
      changeTree.previous();
      myTree.previous();
    }

    changeTree.next(1);
    ClearData(changeTree);
    MakeData(changeTree, 2);
    changeTree.previous();
    CleanChange(changeTree);

    FillHeaderChange(myTree, changeTree);
    //changeTree.printFullTree();
    //RemoveHeader4Now(changeTree);

    ChangeDocs.add(changeTree);
  }

  private void FillLineItems(PDSTree Primary, PDSTree Secondary){
    Primary.getNodeByAttrNameAll("ContractLineItems");
    Secondary.getNodeByAttrNameAll("ContractLineItems");
    Primary.next(0);
    Secondary.next(0);
    Primary.deleteChild(0);
    Primary.deleteChild(1);
    Secondary.deleteChild(0);
    Secondary.deleteChild(0);
    Primary.next(0);
    Secondary.next(0);

    for(int i = 0; i < Primary.getChildrenSize(); i++){
      Primary.next(i);
      Secondary.next(i);
      RandomFillDouble(Primary, Secondary);
      Primary.previous();
      Secondary.previous();
    }
  }

  private void RandomFillDouble(PDSTree Primary, PDSTree Secondary){
    for(int i = 0; i < Primary.getChildrenSize(); i++){
      if(Primary.getChild(i).getType().equals("xs:choice")){
        Random rand = new Random();
        int temp = rand.nextInt(Primary.getChild(i).getChildrenSize());
        Primary.getChild(i).getChild(temp).setParent(Primary.getCurrentNode());
        Primary.addChild(Primary.getChild(i).getChild(temp));
        Primary.deleteChild(i);
        Secondary.getChild(i).getChild(temp).setParent(Secondary.getCurrentNode());
        Secondary.addChild(Secondary.getChild(i).getChild(temp));
        Secondary.deleteChild(i);
        i -= 1;
      }else if(Primary.getChild(i).getChildrenSize() == 0){
        CollapseData(Primary.getChild(i), 1);
        Secondary.getChild(i).setData(Primary.getChild(i).getData());
      }else{
        Primary.next(i);
        Secondary.next(i);
        RandomFillDouble(Primary, Secondary);
        Primary.previous();
        Secondary.previous();
      }
    }
  }

  private void RandomFillSingle(PDSTree Primary){
    for(int i = 0; i < Primary.getChildrenSize(); i++){
      if(Primary.getChild(i).getType().equals("xs:choice")){
        Random rand = new Random();
        int temp = rand.nextInt(Primary.getChild(i).getChildrenSize());
        Primary.getChild(i).getChild(temp).setParent(Primary.getCurrentNode());
        Primary.addChild(Primary.getChild(i).getChild(temp));
        Primary.deleteChild(i);
        i -= 1;
      }else if(Primary.getChild(i).getChildrenSize() == 0){
        CollapseData(Primary.getChild(i), 2);
      }else{
        Primary.next(i);
        RandomFillSingle(Primary);
        Primary.previous();
      }
    }
  }

  private void RegFillHeaderInfoAddDel(PDSTree Primary, PDSTree Secondary, int child){
    Primary.next(child); Primary.deleteChild(0); Primary.deleteChild(1); Primary.next(0);
    Secondary.next(child); Secondary.deleteChild(0); Secondary.deleteChild(0); Secondary.next(0);
    RandomFillDouble(Primary, Secondary);
    Primary.previous(); Primary.previous();
    Secondary.previous(); Secondary.previous();
  }

  private void FillHeaderAddDel(PDSTree Primary, PDSTree Secondary, PDSTree myTree){
    Primary.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Secondary.getNodeByAttrNameAll("ProcurementInstrumentHeader");

    // Handeling ClauseInformation (deleting RegulationURL here)
    Primary.next(1); Primary.deleteChild(0); Primary.next(0); Primary.deleteChild(0); Primary.deleteChild(1); Primary.next(0);
    Secondary.next(1); Secondary.deleteChild(0); Secondary.next(0); Secondary.deleteChild(0); Secondary.deleteChild(0); Secondary.next(0);
    RandomFillDouble(Primary, Secondary);
    Primary.previous(); Primary.previous(); Primary.previous();
    Secondary.previous(); Secondary.previous(); Secondary.previous();
    // Handeling ConstructionProjectModificationDetails
    RegFillHeaderInfoAddDel(Primary, Secondary, 2);
    // Skipping ModificationDetails
    Primary.next(3); Primary.deleteChild(0); Primary.deleteChild(0); Primary.deleteChild(0); Primary.deleteChild(0); Primary.deleteChild(0); Primary.deleteChild(0); Primary.deleteChild(0);
    Secondary.next(3); Secondary.deleteChild(0); Secondary.deleteChild(0); Secondary.deleteChild(0); Secondary.deleteChild(0); Secondary.deleteChild(0); Secondary.deleteChild(0); Secondary.deleteChild(0);
    Primary.previous();
    Secondary.previous();
    // Handeling PaymentDiscount
    RegFillHeaderInfoAddDel(Primary, Secondary, 4);
    // Handeling ProcurementInstrumentAttachments
    RegFillHeaderInfoAddDel(Primary, Secondary, 5);
    // Handeling RecurringPayments (RecurringPayments can only be changed, only 1 exists)
    Primary.deleteChild(6);
    Secondary.deleteChild(6);
    // Handeling ReferenceNumber
    RegFillHeaderInfoAddDel(Primary, Secondary, 6);
    // Handeling SecurityDetails
    Primary.next(7);
    Secondary.next(7);
    RegFillHeaderInfoAddDel(Primary, Secondary, 0);
    RegFillHeaderInfoAddDel(Primary, Secondary, 1);
    Primary.previous();
    Secondary.previous();
    // Handeling ServiceAllowanceCharges
    RegFillHeaderInfoAddDel(Primary, Secondary, 8);
    // Handeling RequirementsDescription (attrbute change, remove for now)
    Primary.deleteChild(9);
    Secondary.deleteChild(9);
    // Handeling TelecommunicationModificationDetails (can only be cahnged)
    Primary.deleteChild(9);
    Secondary.deleteChild(9);
    // Handeling ProcurementInstrumentIdentifier (can only be changed)
    Primary.next(9);
    Secondary.next(9);
    myTree.getNodeByAttrNameAll("ProcurementInstrumentIdentifier");
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      Primary.deleteChild(0);
      Secondary.deleteChild(0);
      myTree.next(i);
      Primary.addChild(myTree.CloneSubTree());
      Secondary.addChild(myTree.CloneSubTree());
      myTree.previous();
    }
    Primary.addChild("xs:simpleType");
    Primary.next(Primary.getChildrenSize()-1);
    Primary.putAttribute("name", "ProcurementInstrumentModificationIdentifier");
    Primary.setData("A00001");
    Primary.previous();
    Secondary.addChild("xs:simpleType");
    Secondary.next(Secondary.getChildrenSize()-1);
    Secondary.putAttribute("name", "ProcurementInstrumentModificationIdentifier");
    Secondary.setData("A00003");
    Secondary.previous();

    Primary.previous();
    Secondary.previous();
    // Handeling BasicInformation (can only be changed)
    Primary.deleteChild(10);
    Secondary.deleteChild(10);
    // Handeling ModifiedProcurementInstrumentDates (can only be changed)
    Primary.deleteChild(10);
    Secondary.deleteChild(10);
    // Handeling ProcurementInstrumentAddresses (can only be changed)
    Primary.deleteChild(10);
    Secondary.deleteChild(10);
    // Handeling ProcurementInstrumentAmounts
    Primary.next(10);
    Secondary.next(10);
    Primary.next(0);
    Secondary.next(0);
    RandomFillDouble(Primary, Secondary);
    Primary.previous();
    Secondary.previous();
    RegFillHeaderInfoAddDel(Primary, Secondary, 1);
    RegFillHeaderInfoAddDel(Primary, Secondary, 2);
    Primary.next(0);
    Secondary.next(0);
    RandomFillDouble(Primary, Secondary);
    Primary.previous(); Primary.previous();
    Secondary.previous(); Secondary.previous();
    // Handeling Shipping
    Primary.next(11);
    Secondary.next(11);
    RegFillHeaderInfoAddDel(Primary, Secondary, 0);
    RegFillHeaderInfoAddDel(Primary, Secondary, 1);
    RandomFillDouble(Primary, Secondary);
    Primary.previous();
    Secondary.previous();
    // Handeling WageDeterminationDetails
    Primary.next(12);
    Secondary.next(12);
    RegFillHeaderInfoAddDel(Primary, Secondary, 0);
    RegFillHeaderInfoAddDel(Primary, Secondary, 1);
    Primary.previous();
    Secondary.previous();
    // Handeling ProcurementInstrumentDates (can only be changed)
    Primary.deleteChild(13);
    Secondary.deleteChild(13);
    // Handeling DeliveryDetails (can only be changed)
    Primary.deleteChild(13);
    Secondary.deleteChild(13);
    // Handeling OrderingDiscounts
    Primary.next(13);
    Secondary.next(13);
    RegFillHeaderInfoAddDel(Primary, Secondary, 0);
    RegFillHeaderInfoAddDel(Primary, Secondary, 1);
    Primary.previous();
    Secondary.previous();

    // Handeling ProcurementInstrumentIdentifierModificationDetails
    Primary.deleteChild(0);
    Secondary.deleteChild(0);
  }

  private void FillHeaderChange(PDSTree Previous, PDSTree Current){
    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    for(int i = 0; i < 6; i++){
      Current.deleteChild(0);
    }
    for(int i = 0; i < 3; i++){
      Current.deleteChild(1);
    }
    for(int i = 0; i < 3; i++){
      Current.deleteChild(7);
    }
    Current.deleteChild(9);

    if(Previous.getNodeByAttrNameSub("RecurringPayments")){
      Current.getNodeByAttrNameSub("RecurringPayments");
      Current.getChild(0).getChild(0).setParent(Current.getCurrentNode());
      Current.addChild(Current.getChild(0).getChild(0));
      Current.deleteChild(0);
      Current.next(0); Current.next(0);
      Current.setData("RecurringPayments_change_text_here");
      Current.previous();
      Current.next(1);
      RandomFillSingle(Current);
      Current.previous();
      Current.next(2);
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
    }else{
      Current.getNodeByAttrNameSub("RecurringPayments");
      PDSNode badChild = Current.getCurrentNode();
      Current.previous();
      Current.deleteChild(badChild);
    }

    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    if(Previous.getNodeByAttrNameSub("RequirementsDescription")){
      Current.getNodeByAttrNameSub("RequirementsDescription");
      Current.putAttribute("changeText", "RequirementsDescription_change_text");
      Current.putAttribute("changeFlag", "Modified");
      Current.putAttribute("previousValue", Previous.getData());
      Current.setData("New_RequirementsDescription_changed_data_here");
    }else{
      Current.getNodeByAttrNameSub("RequirementsDescription");
      PDSNode badChild = Current.getCurrentNode();
      Current.previous();
      Current.deleteChild(badChild);
    }

    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    if(Previous.getNodeByAttrNameSub("TelecommunicationDetails")){
      Current.getNodeByAttrNameSub("TelecommunicationModificationDetails");
      Current.getChild(0).getChild(0).setParent(Current.getCurrentNode());
      Current.addChild(Current.getChild(0).getChild(0));
      Current.deleteChild(0);
      Current.next(0); Current.next(0);
      Current.setData("TelecommunicationDetails_change_text_here");
      Current.previous();
      Current.next(1);
      RandomFillSingle(Current);
      Current.previous();
      Current.next(2);
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
    }else{
      Current.getNodeByAttrNameSub("TelecommunicationModificationDetails");
      PDSNode badChild = Current.getCurrentNode();
      Current.previous();
      Current.deleteChild(badChild);
    }

    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    if(Previous.getNodeByAttrNameSub("ProcurementInstrumentIdentifier")){
      Current.getNodeByAttrNameSub("ProcurementInstrumentIdentifier");
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
      Current.addChild("xs:simpleType");
      Current.next(Current.getChildrenSize()-1);
      Current.putAttribute("name", "ProcurementInstrumentModificationIdentifier");
      Current.setData("A00002");
    }else{
      Current.getNodeByAttrNameSub("ProcurementInstrumentIdentifier");
      PDSNode badChild = Current.getCurrentNode();
      Current.previous();
      Current.deleteChild(badChild);
    }

    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    if(Previous.getNodeByAttrNameSub("ProcurementInstrumentDates")){
      Current.getNodeByAttrNameSub("ModifiedProcurementInstrumentDates");
      Current.next(0);
      Current.putAttribute("changeText", "ProcurementInstrumentEffectiveDate_change_text");
      Current.putAttribute("changeFlag", "Modified");
      Current.putAttribute("previousValue", Previous.getData());
      Current.setData("New_ProcurementInstrumentEffectiveDate_changed_data_here");
      Current.previous();
      Current.deleteChild(1);
      Previous.getNodeByAttrNameSub("ProcurementInstrumentPeriods");
      Current.addChild(Previous.CloneSubTree());
    }else{
      Current.getNodeByAttrNameSub("ProcurementInstrumentDates");
      PDSNode badChild = Current.getCurrentNode();
      Current.previous();
      Current.deleteChild(badChild);
    }

    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    if(Previous.getNodeByAttrNameSub("ProcurementInstrumentDates")){
      Current.getNodeByAttrNameSub("ProcurementInstrumentDates");
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
    }else{
      Current.getNodeByAttrNameSub("ProcurementInstrumentDates");
      PDSNode badChild = Current.getCurrentNode();
      Current.previous();
      Current.deleteChild(badChild);
    }

    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    if(Previous.getNodeByAttrNameSub("DeliveryDetails")){
      Current.getNodeByAttrNameSub("DeliveryDetails");
      Current.next(0);
      Current.deleteChild(1); Current.deleteChild(1);
      Current.next(0); Current.next(0);
      Current.setData("DeliveryDateChange_text_here");
      Current.previous();
      Current.next(1);
      RandomFillSingle(Current);
      Current.previous();
      Current.next(2);
      Previous.getNodeByAttrNameSub("DeliveryDates");
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
      Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
      Current.previous(); Current.previous(); Current.previous();
      Current.next(1);
      Current.deleteChild(1); Current.deleteChild(1);
      Current.next(0); Current.next(0);
      Current.setData("DeliveryLeadTimeChange_text_here");
      Current.previous();
      Current.next(1);
      RandomFillSingle(Current);
      Current.previous();
      Current.next(2);
      Previous.getNodeByAttrNameSub("DeliveryLeadTime");
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
      Current.previous();
      Current.previous();
      Current.previous();
      Previous.previous();
      Current.deleteChild(2);
      Current.deleteChild(2);
      Current.deleteChild(2);
      Previous.getNodeByAttrNameSub("ExcessDeliveryAction");
      Current.addChild(Previous.CloneSubTree());
      Previous.previous();
      Previous.getNodeByAttrNameSub("DeliverySpecialHandling");
      Current.addChild(Previous.CloneSubTree());
      Previous.previous();
      Previous.getNodeByAttrNameSub("PreDeliveryNotification");
      Current.addChild(Previous.CloneSubTree());
    }else{
      Current.getNodeByAttrNameSub("DeliveryDetails");
      PDSNode badChild = Current.getCurrentNode();
      Current.previous();
      Current.deleteChild(badChild);
    }
    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    if(Previous.getNodeByAttrNameSub("ProcurementInstrumentAddresses")){
      Current.getNodeByAttrNameSub("ProcurementInstrumentAddresses");
      Current.deleteChild(0);
      if(Previous.getNodeByAttrNameSub("AddressDescription")){
        Current.addChild(Previous.CloneSubTree());
      }else{
        Previous.getNodeByAttrNameSub("AlternateAddressDescription");
        Current.addChild(Previous.CloneSubTree());
      }
      Previous.previous();
      Current.next(0);
      Current.getChild(0).getChild(0).setParent(Current.getCurrentNode());
      Current.addChild(Current.getChild(0).getChild(0));
      Current.deleteChild(0);
      Current.next(0); Current.next(0);
      Current.setData("AddressChange_text_here");
      Current.previous();
      Current.next(1);
      RandomFillSingle(Current);
      Current.previous();
      Current.next(2);
      Previous.getNodeByAttrNameSub("Address");
      Current.addChild(Previous.CloneSubTree());
      Previous.previous();
      Current.deleteChild(0);
      Previous.getNodeByAttrNameSub("Contact");
      Current.addChild(Previous.CloneSubTree());
      Previous.previous();
      Current.deleteChild(0);
      Current.previous(); Current.previous(); Current.previous();
      Current.next(1);
      Current.getChild(0).getChild(0).setParent(Current.getCurrentNode());
      Current.addChild(Current.getChild(0).getChild(0));
      Current.deleteChild(0);
      Current.next(0); Current.next(0);
      Current.setData("BusinessClassificationChange_text_here");
      Current.previous();
      Current.next(1);
      RandomFillSingle(Current);
      Current.previous();
      Current.next(2);
      Previous.getNodeByAttrNameSub("BusinessClassification");
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
      Previous.previous();
      Previous.getNodeByAttrNameSub("AcceptanceInspection");
      Current.previous(); Current.previous(); Current.previous();
      Current.next(2);
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
    }else{
      Current.getNodeByAttrNameSub("ProcurementInstrumentAddresses");
      PDSNode badChild = Current.getCurrentNode();
      Current.previous();
      Current.deleteChild(badChild);
    }

    Previous.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    Current.getNodeByAttrNameAll("ProcurementInstrumentHeader");
    if(Previous.getNodeByAttrNameSub("BasicInformation")){
      Current.getNodeByAttrNameSub("BasicInformation");
      Current.next(0);
      Previous.getNodeByAttrNameSub("ContractDescription");
      Current.putAttribute("changeText", "Change_text_here");
      Current.putAttribute("changeFlag", "Modified");
      Current.putAttribute("previousValue", Previous.getData());
      Current.setData("New_ContractDescription_here");
      Previous.previous();
      Current.previous();
      Current.next(1);
      Previous.getNodeByAttrNameSub("ProcurementInstrumentName");
      Current.putAttribute("changeText", "Change_text_here");
      Current.putAttribute("changeFlag", "Modified");
      Current.putAttribute("previousValue", Previous.getData());
      Current.setData("New_ProcurementInstrumentName_here");
      Previous.previous();
      Current.previous();

      Current.next(2);
      Previous.getNodeByAttrNameSub("TransmissionAcknowledgement");
      Current.setData(Previous.getData());
      Current.previous();
      Previous.previous();

      Current.next(3);
      Previous.getNodeByAttrNameSub("ContingencyContract");
      Current.setData(Previous.getData());
      Current.previous();
      Previous.previous();

      Current.next(4);
      Previous.getNodeByAttrNameSub("EmergencyRequestContract");
      Current.setData(Previous.getData());
      Current.previous();
      Previous.previous();

      Current.next(5);
      Previous.getNodeByAttrNameSub("NotFullOpenCompetition");
      Current.setData(Previous.getData());
      Current.previous();
      Previous.previous();

      Current.next(6);
      Current.deleteChild(0);
      Previous.getNodeByAttrNameSub("PricingArrangement");
      Previous.next(0);
      Current.addChild(Previous.CloneSubTree());
      Previous.previous(); Previous.previous();
      Current.previous();

      Current.deleteChild(7);
      Current.next(7);
      Current.getChild(0).getChild(0).setParent(Current.getCurrentNode());
      Current.addChild(Current.getChild(0).getChild(0));
      Current.deleteChild(0);
      Current.next(0); Current.next(0);
      Current.setData("ShareRatioChange_text_here");
      Current.previous();
      Current.next(1);
      RandomFillSingle(Current);
      Current.previous();
      Current.next(2);
      Previous.getNodeByAttrNameSub("ShareRatio");
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
      Previous.previous();
      Current.previous(); Current.previous(); Current.previous();
      Current.next(8);
      Current.next(0);
      Current.setData("SolicitationOfferInformationChange_text_here");
      Current.previous();
      Current.next(1);
      RandomFillSingle(Current);
      Current.previous();
      Current.next(2);
      Previous.getNodeByAttrNameSub("SolicitationOfferInformation");
      for(int i = 0; i < Current.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }
      Previous.previous();
      Current.previous(); Current.previous();


      Current.next(9);
      Previous.getNodeByAttrNameSub("EffortCategory");
      Current.setData(Previous.getData());
      Previous.previous();
      Current.previous();

      Current.next(10);
      Previous.getNodeByAttrNameSub("DocumentPurpose");
      Current.setData(Previous.getData());
      Previous.previous();
      Current.previous();

      Current.next(11);
      Previous.getNodeByAttrNameSub("AwardInstrumentSecurityLevel");
      Current.setData(Previous.getData());
      Previous.previous();
      Current.previous();

      Current.next(12);
      Previous.getNodeByAttrNameSub("MiscellaneousTextDetails");
      for(int i = 0; i < Previous.getChildrenSize(); i++){
        Current.deleteChild(0);
        Previous.next(i);
        Current.addChild(Previous.CloneSubTree());
        Previous.previous();
      }

    }


  }

  private void ClearData(PDSTree myTree){
    myTree.setData("");
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      myTree.next(i);
      ClearData(myTree);
      myTree.previous();
    }
  }

  private void RemoveHeader4Now(PDSTree myTree){
    myTree.getNodeByAttrNameAll("AwardModificationInstrument");
    myTree.deleteChild(1);
  }

  private void CleanChange(PDSTree myTree){
    Random rand = new Random();
    if(!myTree.getNodeByAttrNameAll("LineItemChangeTextDetails")){
      //System.out.println("-!-");
    }else{
      myTree.next(1);
      //myTree.printNodeFull();
      int choice =  rand.nextInt(myTree.getChild(0).getChildrenSize());
      myTree.getChild(0).getChild(choice).setParent(myTree.getCurrentNode());
      myTree.addChild(myTree.getChild(0).getChild(choice));
      myTree.deleteChild(0);
    }
  }

  private void Finalize(PDSTree myTree){
    myTree.gotoRoot();
    if(myTree.getNodeByAttrNameSub("ProcurementInstrumentModificationIdentifier")){
      myTree.deleteNode();
    }


    myTree.gotoRoot();
    myTree.getNodeByAttrNameAll("AwardInstrument");
    int temp = -1;
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      if(myTree.getChild(i).getAttribute("name").equals("ProcurementInstrumentHeader")){
        temp = i;
      }
    }

    if(temp != 0){
      PDSNode tempNode = myTree.getChild(0);
      myTree.deleteChild(tempNode);
      myTree.addChild(tempNode);
    }
  }

}
