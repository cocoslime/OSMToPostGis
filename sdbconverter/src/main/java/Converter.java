import javafx.util.Pair;
import org.w3c.dom.Document;

import java.io.*;
import java.net.URL;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

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
    protected static Map<String, Integer> natural_map;

    protected static int b_count = 0;
    protected static int r_count = 0;
    protected static int water_count = 0;
    protected static int wood_count = 0;

    private static DBManager createDBConnection(String port, String db_name, String user, String passwd) throws Exception{
        String url = "jdbc:postgresql://localhost:"+ port+ "/" +db_name;

        Properties props = new Properties();
        props.put("user", user);
        props.put("password", passwd);

        return new DBManager(url, props);
    }

    private static void insertOSMToDB(NodeList nList) throws Exception {
        int count = 0;
        System.out.println("length : " + nList.getLength());
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                if (nNode.getNodeName().equals("node") ) continue;

                else if (nNode.getNodeName().equals("way") ){
                    if (nNode.hasChildNodes()) {
                        Element eElement = (Element) nNode;
                        NodeList taglist = eElement.getElementsByTagName("tag");

                        String type = getType(taglist);
                        if (type == null) continue;
                        countStat(type);

                        String name = getName(taglist);

                        NodeList nList_in_way = eElement.getElementsByTagName("nd");
                        String geom = gb.makeGeometryTEXT(type, nList_in_way, nc);//nc.getWayGeometry(type, list_in_way);

                        if (geom == null){

                        }
                        else {
                            count ++;
                            printStat(count, false);
                            dbm.insert(type, eElement.getAttribute("id"), name, geom);
                        }
                    }
                }
            }
        }
        printStat(count, true);
        System.out.println("--------------EXECUTING BATCHING--------------");
        int[] result = dbm.execute();

    }

    private static void printStat(int count, boolean all) {
        try {
            if (count % 1000 == 1 || all){
                clearConsole();
                System.out.println("Statistic");
                System.out.println("Building : " + b_count);
                System.out.println("Road : " + r_count);
                System.out.println("Water : " + water_count);
                System.out.println("Wood : " + wood_count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearConsole() {
        for (int i = 0 ; i < 10 ; i++){
            System.out.println();
        }
//        System.out.println('\r');
    }

    private static void countStat(String type) {
        if (type.equals("building")){
            b_count++;
        }
        if (type.equals("road")){
            r_count++;
        }
        if (type.equals("wood")){
            wood_count++;
        }
        if (type.equals("water")){
            water_count++;
        }
    }

    private static String getName(NodeList taglist) {
        for (int temp = 0; temp < taglist.getLength(); temp++) {
            Node tag =taglist.item(temp);
            Element eTag = (Element) tag;
            if (eTag.getAttribute("k").equals("name") ){
                return eTag.getAttribute("v");
            }
        }
        for (int temp = 0; temp < taglist.getLength(); temp++) {
            Node tag =taglist.item(temp);
            Element eTag = (Element) tag;
            if (eTag.getAttribute("k").equals("building") ){
                if (!eTag.getAttribute("v").equals("yes") && !eTag.getAttribute("v").equals("no"))
                    return eTag.getAttribute("v");
            }
            if (eTag.getAttribute("k").equals("road") ){
                if (!eTag.getAttribute("v").equals("yes") && !eTag.getAttribute("v").equals("no"))
                    return eTag.getAttribute("v");
            }
            if (eTag.getAttribute("k").equals("natural")){
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

                int count = building_map.containsKey((eTag.getAttribute("v"))) ? building_map.get((eTag.getAttribute("v"))) : 0;
                count += 1;
                building_map.put((eTag.getAttribute("v")), count);

                String type = TypeManager.getType_Building(eTag.getAttribute("v"));
                if (type != null) return type;
            }
            else if (eTag.getAttribute("k").equals("highway") ){

                String type = TypeManager.getType_Road(eTag.getAttribute("v"));
                if (type != null) return type;
            }
            else if (eTag.getAttribute("k").equals("natural") ){

                int count = natural_map.containsKey((eTag.getAttribute("v"))) ? natural_map.get((eTag.getAttribute("v"))) : 0;
                count += 1;
                natural_map.put((eTag.getAttribute("v")), count);

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
        natural_map = new HashMap<>();
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

            nc = new NodeCollection(osm_path);
            nc.makeNodeMap(osm_path);

            //insertDoc(osm_path);
            insertStream(osm_path);

            System.out.println("\n---------------natural types---------------");
            natural_map.forEach((e,v)->{
                        System.out.println(e + "," + v);
                    }
            );

            System.out.println("\n\n---------------building types---------------");
            building_map.forEach((e,v)->{
                System.out.println(e + "," + v);
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertStream(String osm_path) throws Exception{
        System.out.println("Start inserting DB");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = new FileInputStream(osm_path);
        XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
        streamReader.nextTag();

        int count = 0;
        while (streamReader.hasNext()) {
            boolean isWay = false;
            if (streamReader.isStartElement()) {
                switch (streamReader.getLocalName()) {
                    case "way": {
                        isWay = true;
                        String id = streamReader.getAttributeValue(null,"id");
                        streamReader.next();
                        ArrayList<String> node_list = new ArrayList<>();
                        Map<String, String> tag_list = new HashMap<>();
                        String name;
                        String type;

                        boolean isnext = true;
                        while (isnext) {
                            if(streamReader.isStartElement()){
                                switch (streamReader.getLocalName()){
                                    case "nd":{
                                        node_list.add(streamReader.getAttributeValue(null,"ref"));
                                        break;
                                    }
                                    case "tag":{
                                        if (streamReader.getAttributeValue(null,"k").equals("name")){
                                            tag_list.put(streamReader.getAttributeValue(null,"k"), streamReader.getAttributeValue(null,"v") );
                                        }
                                        if (streamReader.getAttributeValue(null,"k").equals("building")){
                                            tag_list.put(streamReader.getAttributeValue(null,"k"), streamReader.getAttributeValue(null,"v") );
                                        }
                                        else if (streamReader.getAttributeValue(null, "k").equals("highway")){
                                            tag_list.put(streamReader.getAttributeValue(null,"k"), streamReader.getAttributeValue(null,"v") );
                                        }
                                        else if (streamReader.getAttributeValue(null, "k").equals("natural")){
                                            tag_list.put(streamReader.getAttributeValue(null,"k"), streamReader.getAttributeValue(null,"v") );
                                        }
                                        break;
                                    }

                                }
                            }
                            streamReader.next();
                            if(streamReader.isStartElement()){
                                if (!streamReader.getLocalName().equals("nd") && !streamReader.getLocalName().equals("tag")) {
                                    isnext = false;
                                    break;
                                }
                            }
                        }

                        name = getNameStream(tag_list);

                        type = getTypeStream(tag_list);
                        if (type == null) continue;

                        countStat(type);

                        String geom = gb.makeGeometryTEXTStream(type, node_list, nc);

                        if (geom == null){
                            System.out.println(type + " null");
                        }
                        else {
                            count ++;
                            printStat(count, false);
                            dbm.insert(type, id, name, geom);
                        }

                        tag_list.clear();
                        node_list.clear();
                        break;
                    }
                }
            }
            if (!isWay) streamReader.next();
        }
        printStat(count, true);
        System.out.println("--------------EXECUTING BATCHING--------------");
        int[] result = dbm.execute();
    }

    private static String getTypeStream(Map<String, String> tag_list) {
        if (tag_list.containsKey("building")){
            int count = building_map.containsKey(tag_list.get("building")) ? building_map.get((tag_list.get("building"))) : 0;
            count += 1;
            building_map.put(tag_list.get("building"), count);

            String type = TypeManager.getType_Building(tag_list.get("building"));
            if (type != null) return type;
        }
        if (tag_list.containsKey("highway")){
            int count = road_map.containsKey(tag_list.get("highway")) ? road_map.get((tag_list.get("highway"))) : 0;
            count += 1;
            road_map.put(tag_list.get("highway"), count);

            String type = TypeManager.getType_Road(tag_list.get("highway"));
            if (type != null) return type;
        }
        if (tag_list.containsKey("highway")){
            int count = natural_map.containsKey(tag_list.get("natural")) ? natural_map.get((tag_list.get("natural"))) : 0;
            count += 1;
            natural_map.put(tag_list.get("natural"), count);

            String type = TypeManager.getType_Natural(tag_list.get("natural"));
            if (type != null) return type;
        }

        return null;
    }

    private static String getNameStream(Map<String, String> tag_list) {
        if (tag_list.containsKey("name")) return tag_list.get("name");
        else{
            if (tag_list.containsKey("building")){
                if (!tag_list.get("building").equals("yes") && !tag_list.get("building").equals("yes"))
                    return tag_list.get("building");
            }
            if (tag_list.containsKey("road")){
                if (!tag_list.get("road").equals("yes") && !tag_list.get("road").equals("yes"))
                    return tag_list.get("road");
            }
            if (tag_list.containsKey("natural")){
                if (!tag_list.get("natural").equals("yes") && !tag_list.get("natural").equals("yes"))
                    return tag_list.get("natural");
            }
        }

        return "non";
    }

    private static void insertDoc(String osm_path) throws Exception {
        System.out.println("Get Document from osm file");
        Document doc = getDocument(osm_path);

        if (doc.getDocumentElement().hasChildNodes()){
            System.out.println("Start inserting DB");
            insertOSMToDB(doc.getDocumentElement().getElementsByTagName("way"));
        }
    }


}
