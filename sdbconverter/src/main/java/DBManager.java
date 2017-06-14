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
    protected static Statement stmt;
    public DBManager(String url, Properties prop) throws SQLException {

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

    public int[] execute() throws SQLException {
        int[] count = stmt.executeBatch();
        conn.commit();
        return count;
    }

    public void insert(String table, String id, String name, String geom) throws SQLException {



        String sql =
                "INSERT INTO " + schema +"."+
                        table +
                        "(id,name,geom) VALUES (" + id +
                        ",'" + name.replace("'","''") +
                        "'," + geom + ");";
       // System.out.println(sql);
       // stmt.executeUpdate(sql);
        stmt.addBatch(sql);


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

    public void setSchema(String p_schema) {
        schema = p_schema;
    }

    public void init() {
        try{
            stmt = conn.createStatement();
            conn.setAutoCommit(false);

            System.out.println("DROP TABLES");
            dropBuildingTable();
            dropRoadTable();
            dropNaturalTable();

            System.out.println("CREATE TABLES");
            createBuildingTable();
            createRoadTable();
            createNaturalTable();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropBuildingTable() throws SQLException {
        Statement stmt = null;

        stmt = conn.createStatement();
        String sql = "DROP TABLE IF EXISTS " + schema + ".building";
        stmt.executeUpdate(sql);

    }

    private void dropRoadTable() throws SQLException {
        Statement stmt = null;

        stmt = conn.createStatement();
        String sql = "DROP TABLE IF EXISTS " + schema + ".road";
        stmt.executeUpdate(sql);

    }

    private void dropNaturalTable() throws SQLException {

        stmt = conn.createStatement();
        String sql = "DROP TABLE IF EXISTS " + schema + ".natural";
        stmt.executeUpdate(sql);

    }

    private void createNaturalTable() throws SQLException {
        Statement stmt = null;

        stmt = conn.createStatement();
        String road_sql = "CREATE TABLE " + schema + ".natural " +
                "(id VARCHAR(30) not NULL, " +
                " name VARCHAR (100), " +
                " geom GEOMETRY, " +
                " PRIMARY KEY ( id ))";
        stmt.executeUpdate(road_sql);

    }

    private void createRoadTable() throws SQLException {
        Statement stmt = null;

        stmt = conn.createStatement();
        String road_sql = "CREATE TABLE " + schema + ".road " +
                "(id VARCHAR(30) not NULL, " +
                " name VARCHAR (100), " +
                " geom GEOMETRY, " +
                " PRIMARY KEY ( id ))";
        stmt.executeUpdate(road_sql);

    }

    private void createBuildingTable() throws SQLException {
        Statement stmt = null;

        stmt = conn.createStatement();
        String sql = "CREATE TABLE " + schema + ".building " +
                "(id VARCHAR(30) not NULL, " +
                " name VARCHAR (100), " +
                " geom GEOMETRY, " +
                " PRIMARY KEY ( id ))";
        stmt.executeUpdate(sql);

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
