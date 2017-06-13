import org.w3c.dom.Document;

import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Created by stem_dong on 2017-06-12.
 */
public class Converter {
    protected static NodeCollection nc;
    protected static GeometryBuilder gb;
    protected static DBManager dbm;

    protected static Map<String, Integer> building_map;
    protected static Map<String, Integer> road_map;

    private static DBManager createDBConnection(String port, String db_name, String user, String passwd) throws Exception{
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
                if (nNode.getNodeName().equals("node") ) continue;

                else if (nNode.getNodeName().equals("way") ){
                    if (nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList taglist = eElement.getElementsByTagName("tag");

                        String type = getType(taglist);
                        if (type == null) continue;

                        String name = getName(taglist);

                        NodeList nList_in_way = eElement.getElementsByTagName("nd");
                        String geom = gb.makeGeometryTEXT(type, nList_in_way, nc);//nc.getWayGeometry(type, list_in_way);

                        if (geom != null) db.insert(type, eElement.getAttribute("id"), name , geom);
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
                building_map.put((eTag.getAttribute("v") ), 1);
                String type = TypeManager.getType_Building(eTag.getAttribute("v"));
                if (type != null) return type;
            }
            else if (eTag.getAttribute("k").equals("highway") ){
                road_map.put((eTag.getAttribute("v") ), 1);
                String type = TypeManager.getType_Road(eTag.getAttribute("v"));
                if (type != null) return type;
            }
            else if (eTag.getAttribute("k").equals("natural") ){
                String type = TypeManager.getType_Natural(eTag.getAttribute("v"));
                if (type != null) return type;
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
        building_map = new HashMap<>();
        road_map = new HashMap<>();

        try{
            BufferedReader in;
            BufferedReader branswer = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Do you Use input.txt file? (Y/N)");
            String answer = branswer.readLine();

            if (answer.contains("Y") || answer.contains("y")){
                in = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt")));
            }
            else{
                in  = new BufferedReader(new InputStreamReader(System.in));
            }

            System.out.println("Input Port Number");
            String port = in.readLine();

            System.out.println("Input DB name");
            String db_name = in.readLine();

            System.out.println("Input user");
            String user = in.readLine();

            System.out.println("Input password");
            String passwd = in.readLine();
            dbm = createDBConnection(port, db_name, user, passwd);

            System.out.println("Input schema");
            String schema = in.readLine();
            dbm.setSchema(schema);
            dbm.init();

            System.out.println("Input OSM Data path");
            String osm_path = in.readLine();

            System.out.println("Input CRS to transform");
            String crs = in.readLine();
            gb = new GeometryBuilder();
            gb.setCRS(crs);

            Document doc = getDocument(osm_path);
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            nc = new NodeCollection();
            nc.makeNodeMap(osm_path);

            if (doc.getDocumentElement().hasChildNodes()){
                insertOSMToDB(doc.getDocumentElement().getChildNodes(), dbm);
            }


            System.out.println("\n---------------road types---------------");
            road_map.forEach((e,v)->{
                        System.out.println(e);
                    }
            );

            System.out.println("\n\n---------------building types---------------");
            building_map.forEach((e,v)->{
                System.out.println(e);
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
