import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Created by stem_dong on 2017-06-12.
 */
public class Converter {
    protected static NodeCollection nc;

    private static DBManager getDB(String port, String db_name, String user, String passwd) throws Exception{
        String url = "jdbc:postgresql://localhost:"+ port+ "/" +db_name;

        Properties props = new Properties();
        props.put("user", user);
        props.put("password", passwd);

        return new DBManager(url, props);
    }

    private static void insertOSMToDB(NodeList nList, DBManager db) throws Exception {
        System.out.println("length : " + nList.getLength());
        for (int temp = 0; temp < nList.getLength(); temp++) {
            if (temp % 1000 == 0) System.out.println("-----------" + temp + " data --------");
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                if (nNode.getNodeName().equals("node") ){
                    Element eElement = (Element) nNode;
                }
                else if (nNode.getNodeName().equals("way") ){
                    if (nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList taglist = eElement.getElementsByTagName("tag");

                        String type = getType(taglist);
                        if (type == null) continue;

                        String name = getName(taglist);

                        NodeList list_in_way = eElement.getElementsByTagName("nd");
                        String geom = nc.getWayGeometry(type, list_in_way);

                        db.insert(type, eElement.getAttribute("id"), name , geom);
                    }
                }
            }
        }
        System.out.println("----------------------------");
    }

    private static String getName(NodeList taglist) {
        for (int temp = 0; temp < taglist.getLength(); temp++) {
            Node tag =taglist.item(temp);
            Element eTag = (Element) tag;
            if (eTag.getAttribute("k").equals("name") ){
                return eTag.getAttribute("v");
            }
        }
        return "non";
    }

    private static String getType(NodeList taglist) {
        for (int temp = 0; temp < taglist.getLength(); temp++) {
            Node tag =taglist.item(temp);
            Element eTag = (Element) tag;

            if (eTag.getAttribute("k").equals("building") ){
                if (eTag.getAttribute("v").equals("house") )
                    return "building";
                else
                    return "building";
            }
            else if (eTag.getAttribute("k").equals("highway") ){

                if (eTag.getAttribute("v").equals("residential") )
                    return "road";
                else if (eTag.getAttribute("v").equals("footway") )
                    return "road";
                else
                    return "road";
            }

        }
        return null;
    }

    private static Document getDocument(String path) throws ParserConfigurationException, SAXException, IOException {
        File fXmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(fXmlFile);
    }

    public static void main(String[] args){
        try{

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Input Port Number");
            String port = in.readLine();

            System.out.println("Input DB name");
            String db_name = in.readLine();

            System.out.println("Input user");
            String user = in.readLine();

            System.out.println("Input password");
            String passwd = in.readLine();

            System.out.println("Input OSM Data path");
            String osm_path = in.readLine();

            DBManager postgres = getDB(port, db_name, user, passwd);
            postgres.init();

            Document doc = getDocument(osm_path);
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("node");
            nc = new NodeCollection();
            nc.putList(nList);

            if (doc.getDocumentElement().hasChildNodes()){
                insertOSMToDB(doc.getDocumentElement().getChildNodes(), postgres);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
