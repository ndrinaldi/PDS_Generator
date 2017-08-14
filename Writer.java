import java.util.*;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Writer {

  //Writes a PDSTree object to a FileName as an xml
  public void WriteTree (PDSTree myTree, String FileName){

    myTree.gotoRoot();

    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();

      myTree.gotoRoot();
      Element Root = doc.createElement(myTree.getAttribute("name"));
      doc.appendChild(Root);
      for(int i = 0; i < myTree.getChildrenSize(); i++){
        myTree.next(i);
        WriteTreeHelper(myTree, doc, Root);
        myTree.previous();
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(FileName));
      transformer.transform(source, result);

      //Output to console for testing
      // StreamResult consoleResult = new StreamResult(System.out);
      // transformer.transform(source, consoleResult);
      //System.out.println("Write Success");
    } catch (Exception e) {
      System.out.println("Writer Failure");
      //myTree.printFullTree();
      e.printStackTrace();
    }
  }

  //Helper for WriteTree - builds the doc object
  private void WriteTreeHelper(PDSTree myTree, Document doc, Element parent){
    if(myTree.containsAttributeKey("name")){


      Element Node = doc.createElement(myTree.getAttribute("name"));
      if(myTree.getData() != null){
        Node.appendChild(doc.createTextNode(myTree.getData()));
      }else{
        System.out.println("NODE HAS NO DATA:");
        myTree.printNodeFull();
        System.out.println(myTree.getCurrentNode().getAllRestrictions());
      }
      parent.appendChild(Node);

      for(int i = 0; i < myTree.getChildrenSize(); i++){
        myTree.next(i);
        WriteTreeHelper(myTree, doc, Node);
        myTree.previous();
      }
    }else{
      System.out.println("HAS NO NAME:");
      myTree.printNodeFull();
    }
  }


}
