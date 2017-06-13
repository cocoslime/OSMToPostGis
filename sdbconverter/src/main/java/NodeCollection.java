import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by stem_dong on 2017-06-13.
 */
public class NodeCollection {
    private DBManager dbm;
    private String tranform_crs;
    private HashMap<String, Coordinate> nodemap = new HashMap<>();
    public NodeCollection(DBManager p_dbm, String crs){
        tranform_crs = crs;
        dbm = p_dbm;
    }
    public Coordinate getNode(String key){
        return nodemap.get(key);
       // return dbm.getNode(key);
    }

    public void pushNode(String key, Coordinate el) throws SQLException {
        nodemap.put(key, el);
        //dbm.insertNode(key, el);
    }

    public void putList(NodeList nList) throws SQLException {
        System.out.println("make Node Table: " + nList.getLength());
        for (int i = 0 ; i < nList.getLength() ; i++){
            if (i % 1000 == 0) System.out.println("-----" + i + "-----");
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                if (nNode.getNodeName() == "node"){
                    Element eElement = (Element) nNode;
                    pushNode( eElement.getAttribute("id"), new Coordinate(Double.parseDouble(eElement.getAttribute("lat")), Double.parseDouble(eElement.getAttribute("lon")))  );
                }
            }
        }
    }

    public String getWayGeometry(String type, NodeList list_in_way) throws Exception {
        String ret = "ST_Transform(ST_GeomFromText('";
        if (TypeManager.getGeomType(type).equals("POLYGON") ){
            ret += "POLYGON((";
        }
        else if (TypeManager.getGeomType(type).equals("LINESTRING") ){
            ret += "LINESTRING(";
        }
        else{
            throw new Exception("wrong type");
        }

        for (int count = 0 ; count < list_in_way.getLength(); count++){
            Node nNode = list_in_way.item(count);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                Coordinate ref_coord = getNode(eElement.getAttribute("ref"));
                ret += ref_coord.lat;
                ret += " ";
                ret += ref_coord.lon;
            }
            if (count != list_in_way.getLength() - 1 ) ret += ",";
        }

        if (TypeManager.getGeomType(type).equals("POLYGON") ){
            ret += "))";
        }
        else if (TypeManager.getGeomType(type).equals("LINESTRING") ){
            ret += ")";
        }
        else{
            throw new Exception("wrong type");
        }

        ret += "',4326), " + tranform_crs + ")";
        return  ret;
    }
}
