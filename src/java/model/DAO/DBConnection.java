package model.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // *** Chỉnh sửa thông tin kết nối tại đây ***
    private static final String SERVER   = "localhost";
    private static final int    PORT     = 1433;
    private static final String DB_NAME  = "TTKPiano";
    private static final String USER     = "sa";
    private static final String PASSWORD = "12345";  

    private static final String URL = "jdbc:sqlserver://" + SERVER + ":" + PORT
            + ";databaseName=" + DB_NAME
            + ";encrypt=false;trustServerCertificate=true";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver not found. Thêm sqljdbc4.jar vào /WEB-INF/lib/", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
