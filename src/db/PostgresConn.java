package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConn {

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {

            Properties props = new Properties();
            props.setProperty("user", DbConfig.SQL_USER);
            props.setProperty("password", DbConfig.SQL_PASS);

            props.setProperty("sslmode", "require");

            connection = DriverManager.getConnection(DbConfig.SQL_URL, props);
        }
        return connection;
    }

    public static boolean testConnection() {
        try {
            Connection c = getConnection();
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("[PostgresConn] Connection failed: " + e.getMessage());
            return false;
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {}
    }
}