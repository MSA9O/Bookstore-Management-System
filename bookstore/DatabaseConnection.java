package bookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bookstore";
    private static final String USER = "root";
    private static final String PASSWORD = "Mohamed@123";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Main method to test the database connection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection established successfully!");
            } else {
                System.out.println("Failed to establish database connection.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}