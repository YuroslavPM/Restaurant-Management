import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    static Connection conn = null;
    static Connection getConnection(){

        try {
            Class.forName("org.h2.Driver");
            conn= DriverManager.getConnection("jdbc:h2:D:\\JavaDB","yuri","1234");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
