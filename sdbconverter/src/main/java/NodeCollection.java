import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

/**
 * Created by stem_dong on 2017-06-13.
 */
public class NodeCollection {
    private HashMap<String, Element> nodemap = new HashMap<>();

    public Element getNode(String key){
        return nodemap.get(key);
    }

    public void pushNode(String key, Element el){
        nodemap.put(key, el);
    }

    public void putList(NodeList nList) {
        for (int i = 0 ; i < nList.getLength() ; i++){
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                if (nNode.getNodeName() == "node"){
                    Element eElement = (Element) nNode;
                    nodemap.put( eElement.getAttribute("id"), eElement);
                }
            }
        }
    }

    public String getWayGeometry(String type, NodeList list_in_way) throws Exception {
        String ret = "ST_GeomFromText('";
        if (TypeManager.getGeomType(type) == "POLYGON"){
            ret += "POLYGON((";
        }
        else if (TypeManager.getGeomType(type) == "LINESTRING"){
            ret += "LINESTRING(";
        }
        else{
            throw new Exception("wrong type");
        }

        for (int count = 0 ; count < list_in_way.getLength(); count++){
            Node nNode = list_in_way.item(count);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                Element refNode = nodemap.get(eElement.getAttribute("ref"));
                ret += refNode.getAttribute("lat");
                ret += " ";
                ret += refNode.getAttribute("lon");
            }
            if (count != list_in_way.getLength() - 1 ) ret += ",";
        }

        if (TypeManager.getGeomType(type) == "POLYGON"){
            ret += "))";
        }
        else if (TypeManager.getGeomType(type) == "LINESTRING"){
            ret += ")";
        }
        else{
            throw new Exception("wrong type");
        }

        ret += "')";
        return  ret;
    }
}
