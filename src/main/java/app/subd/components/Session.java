package app.subd.components;

import app.subd.Database_functions;

import java.sql.Connection;
import java.sql.SQLException;

public class Session {
    private static String username;
    private static String password;
    private static String role;
    private static Connection connection;

    public static void setUser(String username, String password, String role, Connection connection) {
        Session.username = username;
        Session.password = password;
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

    public static void RefreshConnection() {
        if (connection != null) {
            connection = Database_functions.ConnectToDatabase(username, password);
        }
    }

    // Геттеры
    public static String getUsername() { return username; }
    public static String getRole() { return role; }
    public static Connection getConnection() { RefreshConnection(); return connection; }
    
    public static boolean isAdmin() { return "admin_role".equals(role); }
    public static boolean isOwner() { return "owner_role".equals(role); }
    public static boolean isEmployee() { return "employee_role".equals(role); }
}