/**
 * Created by stem_dong on 2017-06-13.
 */
public class TypeManager {
    public static String getGeomType(String tagName){
        if (tagName == "building" || tagName == "house")
            return "POLYGON";
        else if (tagName == "road" || tagName == "footway")
            return "LINESTRING";
        else
            return null;
    }
}
