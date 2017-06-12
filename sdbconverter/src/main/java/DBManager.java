import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by stem_dong on 2017-06-12.
 */
public class DBManager {

    protected static Connection conn = null;

    public static Connection getConnection(String url, Properties prop) throws SQLException {
        if(conn == null || conn.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
            conn = DriverManager.getConnection(url, prop);
        }
        return conn;
    }
}
