package hotel;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            // change username and password for your mysql
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hotel_db", "root", "Manda123aiml");
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return con;
    }
}
