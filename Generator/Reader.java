import java.util.*;
import java.io.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Reader {
	//PRE: PDSForest is an empty list that can contain PDSTree objects
	//POST: PDSForest contains PDSTrees taht are representations of XML objects outlined in "PDS_Schema_v2.5.xsd"
	public void Read(String PDSVersion, List<PDSTree> PDSForest) {

		// Initialize Writer for test output
		try{
	    PrintWriter writer = new PrintWriter("log.txt", "UTF-8");

			// Initialize SAX Parser to fread XSD
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				DefaultHandler handler = new DefaultHandler() {

					int Depth = 0;
					int choiceCounter = 0; // Used the give the choices IDs

					// On start element execute following
					public void startElement(String uri, String localName, String qName, Attributes attributes){
						//Ignoring useless element types
						if(!qName.equals("xs:documentation") && !qName.equals("xs:annotation") && !qName.equals("xs:schema")){

							PDSTree myTree;
							if(Depth == 0){ //Construct a new tree object for a new xsd structure
								myTree = new PDSTree(); PDSForest.add(myTree);
								myTree.addChild(qName);
							}else{ //Add to an existing xsd structure
								myTree = PDSForest.get(PDSForest.size() -1);
								myTree.addChild(qName); //make and add the node witht he specified type
								myTree.next(myTree.getChildrenSize()-1);
								if(qName.equals("xs:choice")){myTree.setData(String.valueOf(choiceCounter)); choiceCounter+=1;} //adding the choice ID
							}

							//Putting the name of the element into the node
							for(int i = 0; i < attributes.getLength(); i++){myTree.putAttribute(attributes.getLocalName(i), attributes.getValue(i));}
							Depth += 1;

						}
					}

					// On end element execute following
					public void endElement(String uri, String localName, String qName){
						//Ignoring useless element types
						if(!qName.equals("xs:documentation") && !qName.equals("xs:annotation") && !qName.equals("xs:schema")){
							//Traverse the object upwards to siginify the end of an element
							PDSTree myTree = PDSForest.get(PDSForest.size() -1);
							myTree.previous();
							Depth -= 1;

						}
					}
				};

				// Parse document and close
				saxParser.parse(PDSVersion, handler);
				writer.close();
				//PDSForest.get(0).printFullTree();

			// Error catch for SAX Parser
			}catch (Exception ex) {
				System.out.print("SAX Error: ");
				ex.printStackTrace();
			}

		// Error catch for Writer
		} catch (IOException e) {
			System.out.print("Writer Error: ");
			System.out.println(e);
		}

	}
}
