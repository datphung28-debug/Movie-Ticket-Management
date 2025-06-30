package movieticketbookingmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {

    private static final String URL = "jdbc:mysql://localhost:3306/moviebook";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    public static Connection connectDb() {
        Connection conn = null;

        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");

            
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Ket noi database thanh cong!");
        } catch (ClassNotFoundException e) {
            System.out.println("Khong tim thay JDBC Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Ket noi database that bai!");
            e.printStackTrace();
        }

        return conn;
    }
}
