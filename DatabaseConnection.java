import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/school_management?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";  // Replace with your database username
    private static final String PASSWORD = "40022275mgg";       // Replace with your database password

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC Driver
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add the MySQL JDBC library to your project.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database. Check your URL, username, and password.");
            e.printStackTrace();
        }
        return conn;
    }
}
