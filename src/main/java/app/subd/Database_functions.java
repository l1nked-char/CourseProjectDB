package app.subd;

import javax.swing.*;
import java.sql.*;
import java.util.Properties;

public class Database_functions {

    public static ResultSet callFunction(Connection conn,
                                               String functionName,
                                               Object... params)
            throws SQLException {
        String sql = String.format("SELECT * FROM public.%s(", functionName);
        StringBuilder placeholders = new StringBuilder();
        placeholders.append("?,".repeat(params.length));
        if (params.length > 0) placeholders.setLength(placeholders.length() - 1);
        sql += placeholders + ")";

        PreparedStatement stmt = conn.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }

    public static Connection ConnectToDatabase(String username, String password) {
        Connection conn = null;
        String url = "jdbc:postgresql://localhost:5432/CourseProject2";
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        try {
            conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Ошибка подключения: " + e.getMessage());
        }
        return conn;
    }
}
