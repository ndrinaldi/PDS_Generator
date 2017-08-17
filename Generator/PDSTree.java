import java.util.*;

public class PDSTree{

  private PDSNode root;
  private PDSNode current_node;
  private PDSNode GLOBALCHEAT = null;

  public PDSTree(){
    root = null;
    current_node = null;
  }

  public PDSTree(PDSNode newRoot){
    root = newRoot;
    current_node = root;
  }

  public PDSTree(String rootName){
    root = new PDSNode(rootName);
    current_node = root;
  }

  // --- NAVIGATION --- //

  // Sets current_node to the root
  public void gotoRoot(){current_node = root;}
  // Iff a node as and attribute 'name' whose value is Destination, return true and set that node to current_node, else reutn false
  public boolean getNodeByAttrNameAll(String Destination){
    PDSNode temp = current_node;
    gotoRoot();
    boolean returnVal = false;
    findNodeHelper(Destination);
    if(GLOBALCHEAT != null){
      current_node = GLOBALCHEAT;
      returnVal = true;
      GLOBALCHEAT = null;
    }else{
      current_node = temp;
      //System.out.println("-!- Could not find node " + Destination +" -!-");
    }
    return(returnVal);
  }

  public boolean getNodeByAttrNameSub(String Destination){
    PDSNode temp = current_node;
    boolean returnVal = false;
    findNodeHelper(Destination);
    if(GLOBALCHEAT != null){
      current_node = GLOBALCHEAT;
      returnVal = true;
      GLOBALCHEAT = null;
    }else{
      current_node = temp;
      //System.out.println("-!- Could not find node " + Destination +" -!-");
    }
    return(returnVal);
  }
  // Helper function for findNode
  public void findNodeHelper(String Destination){
    if(current_node.containsAttributeKey("name")){
      if(current_node.getAttribute("name").equals(Destination)){
        GLOBALCHEAT = current_node;
      }else{
        for(int i = 0; i < getChildrenSize(); i++){
          next(i);
          findNodeHelper(Destination);
          previous();
        }
      }
    }else{
      for(int i = 0; i < getChildrenSize(); i++){
        next(i);
        findNodeHelper(Destination);
        previous();
      }
    }
  }
  // Sets current_node to the child at given index
  public void next(int index){current_node = current_node.getChild(index);}
  // Sets current_node to the parent of current_node
  public void previous(){current_node = current_node.getParent();}

  // --- NODE GETTERS --- //

  // get the data of current_node
  public String getData(){return(current_node.getData());}
  // Get current_node's type
  public String getType(){return(current_node.getType());}
  // return the Value for a secfied Key
  public String getAttribute(String key){return(current_node.getAttribute(key));}
  // returns the unmber of defined attributes
  public int getAttributeSize(){return(current_node.getAttributeSize());}
  // returns a set of the defined keys in attributes
  public Set<String> getAttributeKeys(){return(current_node.getAttributeKeys());}
  // returns a collection of values in attributes
  public Collection<String> getAttributeValues(){return(current_node.getAttributeValues());}
  // returns a set of all attributes
  public Set<Map.Entry<String, String>> getAllAttributes(){return(current_node.getAllAttributes());}
  // returns true iff current_node's attributes contains the specified key
  public boolean containsAttributeKey(String theKey){return(current_node.containsAttributeKey(theKey));}
  // returns true iff current_node's attributes contains the specified value
  public boolean containsAttributeValue(String theValue){return(current_node.containsAttributeValue(theValue));}
  // returns an array of keys from restrictions
  public Set<String> getRestrictionKeys(){return(current_node.getRestrictionKeys());}
  // returns an array of values from restrictions
  public Collection<String> getRestrictionValues(){return(current_node.getRestrictionValues());}
  // returns a set of all key value pairs
  public Set<Map.Entry<String, String>> getAllRestrictions(){return(current_node.getAllRestrictions());}
  // get the restrictionType of current_node
  public String getRestrictionType(){return(current_node.getRestrictionType());}
  // get a single restriction from restrictions
  public String getRestriction(String key){return(current_node.getRestriction(key));}
  // get size of restrictions
  public int getRestrictionSize(){return(current_node.getRestrictionSize());}
  // returns true iff current_node's restrictions contains the specified key
  public boolean containsRestrictionKey(String theKey){return(current_node.containsRestrictionKey(theKey));}
  // returns true iff current_node's restrictions contains the specified value
  public boolean containsRestrictionValue(String theValue){return(current_node.containsRestrictionValue(theValue));}

  // --- TREE GETTERS --- //

  // Returns the root
  public PDSNode getRoot(){return(root);}
  // Gets the current_node
  public PDSNode getCurrentNode(){return(current_node);}
  // returns current_node's parent
  public PDSNode getParent(){return(current_node.getParent());}
  // Get current_node's children
  public List<PDSNode> getChildren(){return(current_node.getChildren());}
  // Sets current_node to one of current_node children specified by indexing the children attribute
  public PDSNode getChild(int index){return(current_node.getChild(index));}
  // returns the number of current_node's children
  public int getChildrenSize(){return(current_node.getChildrenSize());}
  // returns the depth of the current_node
  public int getDepth(){int depth=0; PDSNode tempNode=current_node; while(tempNode.getParent() != null){depth+=1; tempNode = tempNode.getParent();} return(depth);}

  // --- NODE SETTERS --- //

  // set the data for current_node
  public void setData(String newData){current_node.setData(newData);}
  // sets the type of current_node to newType
  public void setType(String newType){current_node.setType(newType);}
  // adds an key value pair to the attributes group
  public void putAttribute(String key, String value){current_node.putAttribute(key, value);}
  // replaces attributes with newMap
  public void putNewAttributes(Map<String, String> newMap){current_node.putAllAttributes(newMap);}
  // removes an attribute from the attributes group
  public void removeAttribute(String key){current_node.removeAttribute(key);}
  // set restrictionType to newRestrictiion
  public void setRestrictionType(String newType){current_node.setRestrictionType(newType);}
  // add a restriction to the restrictions for current_node
  public void putRestriction(String newKey, String newValue){current_node.putRestriction(newKey, newValue);}
  // removes a restrictions
  public void removeRestriction(String key){current_node.removeRestriction(key);}

  // --- MANIPULATORS --- //

  // Set the root
  public void setRoot(String newRoot){PDSNode newNode = new PDSNode(newRoot); root = newNode; current_node = root;}
  // Sets current_node's parent to newParent
  public void setParent(PDSNode newParent){current_node.setParent(newParent);}
  // returns a deep copy of this tree
  public PDSTree CloneFullTree(){
    PDSNode temp = current_node;
    gotoRoot();
    PDSTree NewTree = new PDSTree(CloneSubTree());
    current_node = temp;
    return(NewTree);
  }
  // returns a cloned subtree starting at the current node
  public PDSNode CloneSubTree(){
    PDSNode newNode = current_node.cloneOrphan();
    for(int i = 0; i < getChildrenSize(); i++){
      next(i);
      newNode.addChild(CloneSubTree());
      previous();
      newNode.getChild(i).setParent(newNode);
    }
    return(newNode);
  }
  // Adds a child to the list of current_node's children
  public void addChild(String newName){PDSNode newNode = new PDSNode(current_node, newName); if(root == null){root = newNode; current_node = root;}else{current_node.addChild(newNode);}}
  // Adds a child to the list of current_node's children
  public void addChild(PDSNode newChild){if(root == null){root = newChild; current_node = root;}else{current_node.addChild(newChild); newChild.setParent(current_node);}}
  // removes the child at index from the tree, reassigns subTree appropriately
  public void removeChild(int index){
    PDSNode ToBeGone = getChild(index);
    for(int i = 0; i < ToBeGone.getChildrenSize(); i++){
      ToBeGone.getChild(i).setParent(ToBeGone.getParent());
      ToBeGone.getParent().addChild(ToBeGone.getChild(i));
    }
    deleteChild(index);
  }
  // removes the given child from the tree and reassigns the subtree appropriately
  public void removeChild(PDSNode badChild){
    for(int i = 0; i < badChild.getChildrenSize(); i++){
      badChild.getChild(i).setParent(badChild.getParent());
      badChild.getParent().addChild(badChild.getChild(i));
    }
    deleteChild(badChild);
  }
  // deletes the current_node
  public void deleteNode(){getParent().removeChild(current_node);}
  // Deletes enitre subTree starting at the child at index, returns the Node that was deleted
  public void deleteChild(int index){current_node.removeChild(index);}
  // Deletes entire subTree starting at the child that matches badChild, returns taht node iff it was found and removed
  public void deleteChild(PDSNode badChild){current_node.removeChild(badChild);}


  // --- PRINT --- //

  public void printRoot(){System.out.println(root.getAttribute("name"));}
  public void printNode(){System.out.println(current_node.getAttribute("name"));}
  public void printNodeFull(){
    for (int i = 0; i < getDepth(); i++){System.out.print("  ");}
    System.out.print("<" + getDepth() + " " +  getType()  + " " + getData() + " " + getAllAttributes() + " " +getRestrictionType() + " " + getRestrictionSize() + "> [");
    for(int i = 0; i < getChildrenSize(); i++){System.out.print(getChild(i).getAttribute("name") + ", ");}
    System.out.println("] " );
  }
  public void printDepth(){System.out.println(getDepth());}
  public void printParent(){System.out.println(current_node.getParent().getAttribute("name"));}
  public void printChild(int index){System.out.println(getChild(index).getAttribute("name"));}
  public void printChildren(){for(int i = 0; i < getChildrenSize(); i++){System.out.print(getChild(i).getType() + ", ");}System.out.println();}
  public void printFullTree(){current_node = root; printSubTree();}
  public void printSubTree(){
    printNodeFull();
    //System.out.print("has " + current_node.getChildrenSize() + " children.");
    for(int i = 0; i < current_node.getChildrenSize(); i++){
      next(i);
      printSubTree();
      previous();
    }
  }


}
