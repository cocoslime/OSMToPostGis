import org.w3c.dom.Element;

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
            return "NON";
    }

    public static String getType_Building(String attr){
        if (attr.equals("yes"))
            return "building";
        if (attr.equals("house") )
            return "building";

        if (!attr.equals("no"))
            return "building";

        return null;
    }

    public static String getType_Road(String attr){
        if (attr.equals("residential") )
            return "road";
        if (attr.equals("footway") )
            return "road";
        if (attr.equals("service") )
            return "road";
        if (attr.equals("primary") )
            return "road";
        if (attr.equals("secondary") )
            return "road";

        if (!attr.equals("no"))
            return "road";

        return null;
    }

    public static String getType_Nature(String attr){
        //TODO
        return null;
    }
}
