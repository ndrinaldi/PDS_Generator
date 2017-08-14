import java.util.*;
import java.io.*;

public class Constructor{

  //Turns disjoint PDS trees into one single Master blueprint for the PDS
  public PDSTree Deforest(ArrayList<PDSTree> PDSForest) {
    //Final product
    PDSTree Final = PDSForest.get(0);

    // Conform and Restrict the Forest
    for(int i = 0; i < PDSForest.size(); i++){
      PDSForest.get(i).gotoRoot();
      RemoveEmpties(PDSForest.get(i));
      Restrict(PDSForest.get(i));
    }
    //Consolidate all trees until the leaves of Final are really final
    int result = -1;
    while(result != 0){
      result = Consolidate(Final, PDSForest);
    };
    //Adding attribute groups to elements
    Detail(Final, PDSForest);
    return(Final);
  }


  // Removes all empty wraps from the PDSTree
  private void RemoveEmpties(PDSTree myTree){
    //Traverse myTree
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      myTree.next(i);
      RemoveEmpties(myTree);
      myTree.previous();
    }
    //Remove all nodes matching the specfiied types
    for (int i = 0; i < myTree.getChildrenSize(); i++){
      if (myTree.getChild(i).getType().equals("xs:simpleType") || myTree.getChild(i).getType().equals("xs:complexType") ||
          myTree.getChild(i).getType().equals("xs:complexContent") || myTree.getChild(i).getType().equals("xs:simpleContent") ||
          myTree.getChild(i).getType().equals("xs:sequence")) {
        if(!myTree.getChild(i).containsAttributeKey("name")){
          myTree.removeChild(i);
        }
      }
    }
  }

  // Collapses all restrictions in the restriction attribute of the tree nodes
  private void Restrict(PDSTree myTree){
    //Traverse myTree
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      myTree.next(i);
      Restrict(myTree);
      myTree.previous();
    }
    //Move resrictions definitions from the attributes group to the restrictions group
    if(myTree.getAttribute("type") != null){
      if(myTree.getAttribute("type").equals("xs:string") || myTree.getAttribute("type").equals("xs:date") || myTree.getAttribute("type").equals("xs:positiveInteger") ||
         myTree.getAttribute("type").equals("xs:gYearMonth") || myTree.getAttribute("type").equals("xs:base64Binary") || myTree.getAttribute("type").equals("xs:nonNegativeInteger")){
        myTree.setRestrictionType(myTree.getAttribute("type").split(":")[1]); myTree.removeAttribute("type");
      }
    }
    //Moving restriction definitions from child nodes to proper restrcitions group
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      if(myTree.getChild(i).getType().equals("xs:restriction")){
        myTree.setRestrictionType(myTree.getChild(i).getAttribute("base").split(":")[1]);
        for(int j = 0; j < myTree.getChild(i).getChildrenSize(); j ++){
          myTree.putRestriction(myTree.getChild(i).getChild(j).getType().split(":")[1] + j, myTree.getChild(i).getChild(j).getAttribute("value"));
        }
        myTree.deleteChild(i);
      }else if(myTree.getChild(i).getType().equals("xs:extension")){
        if(myTree.getChild(i).getAttribute("base").equals("xs:string") || myTree.getChild(i).getAttribute("base").equals("xs:time") ||
           myTree.getChild(i).getAttribute("base").equals("xs:date") || myTree.getChild(i).getAttribute("base").equals("xs:positiveInteger")){
          myTree.setRestrictionType(myTree.getChild(i).getAttribute("base").split(":")[1]);
          myTree.removeChild(i);
        }
      }
    }
  }

  // Consolidates Forest into a single mega tree
  private int Consolidate(PDSTree myTree, ArrayList<PDSTree> PDSForest){
    //Traverse Tree
    int count = 0;
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      myTree.next(i);
      count += Consolidate(myTree, PDSForest);
      myTree.previous();
    }

    int NumOfChildren = myTree.getChildrenSize();

    for(int j = 0; j < NumOfChildren; j++){
      for(int i = 0; i < PDSForest.size(); i++){
        //Connecting leaves to their cooresponding roots pairs
        if(myTree.getChild(j).getAttribute("type") != null && myTree.getChild(j).getChildrenSize() == 0){
          if(myTree.getChild(j).getAttribute("type").equals(PDSForest.get(i).getRoot().getAttribute("name"))){
            PDSForest.get(i).gotoRoot();
            PDSNode newNode = PDSForest.get(i).CloneSubTree();
            newNode.putAttribute("name", myTree.getChild(j).getAttribute("name"));
            newNode.setParent(myTree.getCurrentNode());
            myTree.addChild(newNode);
            myTree.deleteChild(j);
            count += 1;
          }
        }else
        //Doing a similar process for "extensions" and "bases"
        if(myTree.getChild(j).getAttribute("base") != null){
          if(myTree.getChild(j).getAttribute("base").equals(PDSForest.get(i).getRoot().getAttribute("name"))){
            PDSForest.get(i).gotoRoot();
            PDSNode newNode = PDSForest.get(i).CloneSubTree();
            newNode.putAttribute("name", myTree.getAttribute("name"));
            newNode.setParent(myTree.getCurrentNode().getParent());
            PDSNode badChild = myTree.getCurrentNode();
            myTree.previous();
            myTree.addChild(newNode);
            myTree.deleteChild(badChild);

            int tempindex = -1;
            for(int k = 0; k < myTree.getChildrenSize(); k++){
              if(myTree.getChild(k) == newNode){
                tempindex = k;
              }
            }
            myTree.next(tempindex);
            count += 1;
            i = PDSForest.size()+1;
          }
        }
        //Secret addition using 'ref' is now accounted for
        else if(myTree.getChild(j).getType().equals("xs:group") || myTree.getChild(j).getType().equals("xs:attribute")){
          if(myTree.getChild(j).getAttribute("ref") != null){
            if(myTree.getChild(j).getAttribute("ref").equals(PDSForest.get(i).getRoot().getAttribute("name"))){
              PDSForest.get(i).gotoRoot();
              PDSNode newNode = PDSForest.get(i).CloneSubTree();
              newNode.setParent(myTree.getCurrentNode());
              myTree.addChild(newNode);
              myTree.deleteChild(j);
              count += 1;
            }
          }
        }
      }
    }
    return(count);
  }

  // Add attribute groups to the attribute group (useless comment, adding attributes)
  private void Detail(PDSTree myTree, ArrayList<PDSTree> PDSForest){
    for(int i = 0; i < myTree.getChildrenSize(); i++){
      myTree.next(i);
      Detail(myTree, PDSForest);
      myTree.previous();
    }
    if(myTree.getType() == "xs:attributeGroup"){
      for(int i = 0; i < PDSForest.size(); i++){
        //System.out.println("Comparing " + myTree.getAttribute("ref") + " and " + PDSForest.get(i).getRoot().getAttribute("name"));
        if(myTree.getAttribute("ref").equals(PDSForest.get(i).getRoot().getAttribute("name"))){
          for(int j = 0; j < PDSForest.get(i).getChildrenSize(); j++){
            myTree.getParent().putAttribute(PDSForest.get(i).getChild(j).getAttribute("name"), "TBA");
          }
        }
      }
      myTree.deleteNode();
    }
  }

}
