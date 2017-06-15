import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by stem_dong on 2017-06-13.
 */
public class NodeCollection {
    private HashMap<Long, Coordinate> nodemap = new HashMap<>();
    protected String path;
    public NodeCollection(String path){
        this.path = path;
    }

    public Coordinate getNode(String key) throws Exception{
//        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
//        InputStream in = new FileInputStream(path);
//        XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
//        streamReader.nextTag();
//
//        int count = 0;
//        while (streamReader.hasNext()) {
//            if (streamReader.isStartElement()) {
//                switch (streamReader.getLocalName()) {
//                    case "node": {
//                        if (streamReader.getAttributeValue(null,"id").equals(key) ){
//                            return new Coordinate(Double.parseDouble(streamReader.getAttributeValue(null,"lat")),
//                                    Double.parseDouble(streamReader.getAttributeValue(null,"lon")) );
//                        }
//
//                        //break;
//                    }
//                }
//            }
//            streamReader.next();
//        }
//        System.out.println("node " + key + " is not in osm data");
//        return null;
        return nodemap.get(Long.parseLong(key) );
    }

    public void pushNode(Long key, Coordinate el) throws SQLException {
        nodemap.put(key, el);
    }

    public void makeNodeMap(String osm_path) throws Exception {
        System.out.println("---------------make Node Map--------------");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = new FileInputStream(osm_path);
        XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);
        streamReader.nextTag();

        int count = 0;
        while (streamReader.hasNext()) {
            if (count % 100000 == 0) System.out.println("--------- " + count + " done --------");
            count++;

            if (streamReader.isStartElement()) {
                switch (streamReader.getLocalName()) {
                    case "node": {
                        pushNode( Long.parseLong(streamReader.getAttributeValue(null,"id") ),
                                new Coordinate(Double.parseDouble(streamReader.getAttributeValue(null,"lat")),
                                        Double.parseDouble(streamReader.getAttributeValue(null,"lon")))  );
                        break;
                    }
                }
            }
            streamReader.next();
        }
    }
}
