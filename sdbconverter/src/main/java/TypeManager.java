/**
 * Created by stem_dong on 2017-06-13.
 */
public class TypeManager {
    public static String getGeomType(String tagName){
        if (tagName.equals("building") || tagName.equals("house") )
            return "POLYGON";
        else if (tagName.equals("road") || tagName.equals("footway") )
            return "LINESTRING";
        else
            return null;
    }
}
