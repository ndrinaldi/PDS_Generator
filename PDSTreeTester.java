import java.util.*;

public class PDSTreeTester {

  public static void main(String args[]){
    //System.out.println("Started Test");
    PDSTree Groot = new PDSTree();
    //System.out.println("Tree Made");
    for(int i = 1; i < 10; i++){
      String str = Integer.toString(i);
      Groot.addChild(str);
      if(i != 1){Groot.next(0);Groot.putAttribute("name", "name" + i);}
    }

  Groot.gotoRoot();

  PDSNode clone = Groot.getRoot().cloneOrphan();
  PDSTree babyGroot = Groot.CloneFullTree();

  babyGroot.next(0);
  babyGroot.next(0);
  babyGroot.putAttribute("test", "test");

  //Groot.printFullTree();
  System.out.println("then");
  //babyGroot.printFullTree();
  babyGroot.findNodeByAttrName("name9");
  babyGroot.findNodeByAttrName("name10");
//  babyGroot.printNodeFull();
  }
}
