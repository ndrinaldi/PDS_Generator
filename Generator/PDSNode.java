 import java.util.*;

public class PDSNode {

    private PDSNode parent; // PDSNode parent.
    private ArrayList<PDSNode> children; // a list of the children.

    private String data; // data for this node
    private String type; // The type of element this node is.
    private Map<String, String> attributes; // All of the attributes associated with this node.
    private String restrictionType; // The type of restriction on the data
    private Map<String, String> restrictions; // The definition of the restriction

    // Constructor with no parameters
    public PDSNode(){
      parent = null;
      children = new ArrayList<PDSNode>();
      data = "";
      type = null;
      attributes = new HashMap<String, String>();
      restrictionType = null;
      restrictions = new HashMap<String, String>();
    }
    // Constructor for the root node
    public PDSNode(String myType){
      parent = null;
      children = new ArrayList<PDSNode>();
      data = "";
      type = myType;
      attributes = new HashMap<String, String>();
      restrictionType = null;
      restrictions = new HashMap<String, String>();
    }
    // Constructor with standard parameters
    public PDSNode(PDSNode myParent, String myType){
      parent = myParent;
      children = new ArrayList<PDSNode>();
      data = "";
      type = myType;
      attributes = new HashMap<String, String>();
      restrictionType = null;
      restrictions = new HashMap<String, String>();
    }

    // copy
    public PDSNode cloneOrphan(){
      PDSNode newNode = new PDSNode();
      newNode.putAllAttributes(attributes);
      newNode.setData(data);
      newNode.setType(type);
      newNode.setRestrictionType(restrictionType);
      newNode.putAllRestrictions(restrictions);
      return(newNode);
    }

    // Data methods
    public String getData(){return(data);}
    public void setData(String newData){data = newData;}

    // Type methods
    public String getType(){return(type);}
    public void setType(String newType){type = newType;}

    // Attribute methods
    public String getAttribute(String key){return(attributes.get(key));}
    public int getAttributeSize(){return(attributes.size());}
    public Set<String> getAttributeKeys(){return(attributes.keySet());}
    public Collection<String> getAttributeValues(){return(attributes.values());}
    public Set<Map.Entry<String, String>> getAllAttributes(){return(attributes.entrySet());}
    public boolean containsAttributeKey(String theKey){return(attributes.containsKey(theKey));}
    public boolean containsAttributeValue(String theValue){return(attributes.containsValue(theValue));}
    public void putAttribute(String key, String value){attributes.put(key, value);}
    public void removeAttribute(String key){attributes.remove(key);}
    public void putAllAttributes(Map<String, String> newMap){attributes.putAll(newMap);}

    // Restriction methods
    public String getRestrictionType(){return(restrictionType);}
    public void setRestrictionType(String newType){restrictionType = newType;}
    public void removeRestriction(String key){restrictions.remove(key);}
    public Set<String> getRestrictionKeys(){return(restrictions.keySet());}
    public Collection<String> getRestrictionValues(){return(restrictions.values());}
    public Set<Map.Entry<String, String>> getAllRestrictions(){return(restrictions.entrySet());}
    public String getRestriction(String key){return(restrictions.get(key));}
    public void putRestriction(String newKey, String newValue){restrictions.put(newKey, newValue);}
    public int getRestrictionSize(){return(restrictions.size());}
    public void putAllRestrictions(Map<String, String> newMap){restrictions.putAll(newMap);}
    public boolean containsRestrictionKey(String theKey){return(restrictions.containsKey(theKey));}
    public boolean containsRestrictionValue(String theValue){return(restrictions.containsValue(theValue));}

    // Parent methods
    public PDSNode getParent(){return(parent);}
    public void setParent(PDSNode newParent){parent = newParent;}

    // Children methods
    public List<PDSNode> getChildren(){return(children);}
    public PDSNode getChild(int index){return(children.get(index));}
    public void addChild(PDSNode newChild){children.add(newChild);}
    public void removeChild(int index){children.remove(index);}
    public void removeChild(PDSNode badChild){children.remove(badChild);}
    public int getChildrenSize(){return(children.size());}

}
