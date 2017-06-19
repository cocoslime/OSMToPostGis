import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by stem_dong on 2017-06-13.
 */
public class GeometryBuilder {
    protected String CRS = "4326";

    public GeometryBuilder() {

    }

    public void setCRS(String p_crs){
        this.CRS = p_crs;
    }


    public String makeGeometryTEXT(String type, NodeList nList_in_way, NodeCollection nc) throws Exception {
        String ret = "ST_Transform(ST_GeomFromText('";
        if (TypeManager.getGeomType(type).equals("POLYGON") ){
            ret += "POLYGON((";

            Element first = (Element)nList_in_way.item(0);
            Element end = (Element)nList_in_way.item(nList_in_way.getLength()-1);
            if (!first.getAttribute("ref").equals(end.getAttribute("ref"))){
                return null; //POLYGON Not Closed
            }
        }
        else if (TypeManager.getGeomType(type).equals("LINESTRING") ){
            ret += "LINESTRING(";
        }
        else{
            throw new Exception("wrong type");
        }

        for (int count = 0 ; count < nList_in_way.getLength(); count++){
            Node nNode = nList_in_way.item(count);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                Coordinate ref_coord = nc.getNode(eElement.getAttribute("ref"));
                if (ref_coord == null) return null;
                ret += ref_coord.lon;
                ret += " ";
                ret += ref_coord.lat;
            }
            if (count != nList_in_way.getLength() - 1 ) ret += ",";
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

        ret += "',4326), " + CRS + ")";
        return  ret;
    }

    public String makeGeometryTEXTStream(String type, ArrayList<String> node_list, NodeCollection nc) throws Exception  {
        String ret = "ST_Transform(ST_GeomFromText('";
        if (TypeManager.getGeomType(type).equals("POLYGON") ){
            ret += "POLYGON((";

            String first = node_list.get(0);
            String end = node_list.get(node_list.size() - 1);
            if (!first.equals(end)){
                return null; //POLYGON Not Closed
            }

        }
        else if (TypeManager.getGeomType(type).equals("LINESTRING") ){
            ret += "LINESTRING(";
        }
        else{
            throw new Exception("wrong type");
        }

        for (int count = 0 ; count < node_list.size(); count++){

            Coordinate ref_coord = nc.getNode(node_list.get(count));
            if (ref_coord == null) return null;
            ret += ref_coord.lon;
            ret += " ";
            ret += ref_coord.lat;

            if (count != node_list.size() - 1 ) ret += ",";
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

        ret += "',4326), " + CRS + ")";
        return  ret;
    }
}
