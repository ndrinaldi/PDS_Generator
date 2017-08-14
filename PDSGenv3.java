import java.util.*;
import java.io.*;

public class PDSGenv3{

  public static void main(String args[]) {

    ArrayList<PDSTree> Forest_25 = new ArrayList<PDSTree>();

    Reader reader = new Reader();
    Constructor constructor = new Constructor();
    Generator generator = new Generator();
    Writer writer = new Writer();

    reader.Read("PDS_Schema_v2.5.xsd", Forest_25);     //Reads the XSD into multiple PDSTree ojbects
    PDSTree PDS_25 = constructor.Deforest(Forest_25);  //Consolidates PDSTree objects into a single Master blueprint PDSTree
    PDSTree PDS_25_forMods = PDS_25.CloneFullTree();   //Sperate the blueprint into an Award tree and Modification tree
    generator.GenerateAwards(PDS_25);                  //Generate Award Docments so that every data field is expressed at least once, the writes the tree to a file as an xml
    generator.GenerateMods(PDS_25_forMods);            //Generate Add/Change/Delete Modification Documents that coorespond with the Award Documents

    //PDS_25.printFullTree()

  }
}
