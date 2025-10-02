package app.subd;

import java.sql.Connection;
import java.sql.SQLException;

// Session.java
public class Session {
    private static String username;
    private static String role;
    private static Connection connection;

    public static void setUser(String username, String role, Connection connection) {
        Session.username = username;
        Session.role = role;
        Session.connection = connection;
    }

    public static void clear() {
        username = null;
        role = null;
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connection = null;
    }

    // Геттеры
    public static String getUsername() { return username; }
    public static String getRole() { return role; }
    public static Connection getConnection() { return connection; }
    
    public static boolean isAdmin() { return "admin_role".equals(role); }
    public static boolean isOwner() { return "owner_role".equals(role); }
    public static boolean isEmployee() { return "employee_role".equals(role); }
}