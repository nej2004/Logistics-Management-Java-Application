package fasttracklogistics.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // !!! IMPORTANT: Replace these with your actual database credentials !!!
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fasttrack_logistics?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "fasttrack_user"; // Your database username
    private static final String PASS = "devcorps_2025"; // Your database password

    public static Connection getConnection() throws SQLException {
        try {
            // Ensure the JDBC driver is loaded. For modern JDBC (Java 6+), this is often not strictly
            // necessary as DriverManager automatically finds drivers, but it's good practice.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Make sure the JAR is in your project's classpath.");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not available.", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * Helper method to close a database connection.
     * @param conn The Connection object to close.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}