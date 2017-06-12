import org.w3c.dom.Document;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * Created by stem_dong on 2017-06-12.
 */
public class Converter {
    public static void main(String[] args){
        try{
            File fXmlFile = new File("map_1.osm");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("node");
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("node id : " + eElement.getAttribute("id"));
                    System.out.println("lat : " + eElement.getAttribute("lat"));
                    System.out.println("long : " + eElement.getAttribute("lon"));

                }
            }
            System.out.println("----------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
