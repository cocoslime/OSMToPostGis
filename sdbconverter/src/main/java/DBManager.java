import java.sql.*;
import java.util.Properties;
import org.postgis.*;
import org.postgresql.*;

/**
 * Created by stem_dong on 2017-06-12.
 */
public class DBManager {

    protected static Connection conn = null;
    protected static String schema;

    public DBManager(String url, Properties prop, String p_schema) throws SQLException {
        schema = p_schema;
        if(conn == null || conn.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
            conn = DriverManager.getConnection(url, prop);
        }
    }

    public Connection getConnection(){
        return conn;
    }


    public void insert(String table, String id, String name, String geom) throws SQLException {

        Statement stmt = conn.createStatement();

        String sql =
                "INSERT INTO " + schema +"."+
                        table +
                        "(id,name,geom) VALUES (" + id +
                        ",'" + name.replace("'","''") +
                        "'," + geom + ");";

        stmt.executeUpdate(sql);

//        PreparedStatement st = null;
//
//        String prepared_insert_ref =
//                "INSERT INTO " + table + "(id, name, geom) VALUES (?, ?, ?);";
//        st = conn.prepareStatement(prepared_insert_ref);
//
//        st.setString(1, id);
//        st.setString(2, name);
//        st.setObject(3, dna.getLength());
//        st.setString(4, dna.getSequenceAsString());
//
//        st.execute();
    }

    public void init() {
        dropTables();
        createTables();
    }

    private void createTables(){
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "CREATE TABLE " + schema + ".building " +
                    "(id VARCHAR(30) not NULL, " +
                    " name VARCHAR (50), " +
                    " geom GEOMETRY, " +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(sql);

            stmt = conn.createStatement();
            String road_sql = "CREATE TABLE " + schema + ".road " +
                    "(id VARCHAR(30) not NULL, " +
                    " name VARCHAR (50), " +
                    " geom GEOMETRY, " +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(road_sql);

            stmt = conn.createStatement();
            String node_sql = "CREATE TABLE " + schema + ".node " +
                    "(id VARCHAR(30) not NULL, " +
                    " lat float, " +
                    " lon float, " +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(node_sql);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropTables() {
        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS " + schema + ".building";
            stmt.executeUpdate(sql);

            stmt = conn.createStatement();
            sql = "DROP TABLE IF EXISTS " + schema + ".road";
            stmt.executeUpdate(sql);

            stmt = conn.createStatement();
            sql = "DROP TABLE IF EXISTS " + schema + ".node";
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void insertNode(String id, Coordinate coord) throws SQLException {

        Statement stmt = conn.createStatement();

        String sql = "INSERT INTO node" +
                "(id,lat,lon) VALUES (" + id +
                "," + Double.toString(coord.lat) +
                "," + Double.toString(coord.lon) + ");";

        stmt.executeUpdate(sql);
    }

    public Coordinate getNode(String key) {
        Statement stmt = null;

        Coordinate coord = new Coordinate();
        try{
            stmt = conn.createStatement();
            String sql = "SELECT id, lat, lon FROM Registration WHERE id ==" + key;
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                coord.lat = rs.getDouble("lat");
                coord.lon = rs.getDouble("lon");
            }
            rs.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return coord;
    }
}
