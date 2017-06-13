import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
            if (first.getAttribute("ref") != end.getAttribute("ref")){
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
                ret += ref_coord.lat;
                ret += " ";
                ret += ref_coord.lon;
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
}
