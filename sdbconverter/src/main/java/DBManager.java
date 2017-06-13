import java.sql.*;
import java.util.Properties;

/**
 * Created by stem_dong on 2017-06-12.
 */
public class DBManager {

    protected static Connection conn = null;

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

    public void insert(String table, int id, String name, String geom) throws SQLException {

        Statement stmt = conn.createStatement();

        String sql =
                "INSERT INTO " +
                        table +
                        "(id,name,geom) VALUES (" + Integer.toString(id) +
                        ",'" + name +
                        "'," + geom + ");";

        //System.out.println(sql);
        stmt.executeUpdate(sql);
    }

    public void init() {
        dropTables();
        createTables();
    }

    private void createTables(){
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "CREATE TABLE building " +
                    "(id INTEGER not NULL, " +
                    " name VARCHAR (50), " +
                    " geom GEOMETRY, " +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(sql);

            stmt = conn.createStatement();
            String road_sql = "CREATE TABLE road " +
                    "(id INTEGER not NULL, " +
                    " name VARCHAR (50), " +
                    " geom GEOMETRY, " +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(road_sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void dropTables() {
        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS building";
            stmt.executeUpdate(sql);

            stmt = conn.createStatement();
            sql = "DROP TABLE IF EXISTS road";
            stmt.executeUpdate(sql);

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
