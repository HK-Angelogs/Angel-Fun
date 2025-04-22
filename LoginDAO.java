package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.DatabaseManager;


public class LoginDAO {
    
    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM Credentials WHERE username = ? AND password = ?"; 
        // For production, you should store hashed passwords, not plain text.

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // If we find a row, credentials match
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
