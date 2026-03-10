package dataaccess;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class MySqlAuth {
    public boolean createAuthToken(int userId, String token, LocalDateTime expiresAt) {
        String sql = "INSERT INTO auth (user_id, token, expires_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setTimestamp(3, Timestamp.valueOf(expiresAt));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating auth token: " + e.getMessage());
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Integer> getUserIdByToken(String token) {
        String sql = "SELECT user_id FROM auth WHERE token = ? AND expires_at > NOW()";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(rs.getInt("user_id"));
            return Optional.empty();
        } catch (SQLException e) {
            System.out.println("Error fetching user ID by token: " + e.getMessage());
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteToken(String token) {
        String sql = "DELETE FROM auth WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting token: " + e.getMessage());
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

