package dataaccess;

import java.sql.*;
import java.util.Optional;
import org.mindrot.jbcrypt.*;

public class MySqlUser {

    public boolean makeUser(String username, String clearTextPassword, String email) {
        String hashPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        String sql = "INSERT INTO user (username, hashed_password, email) VALUES (?, ?, ?)";
        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement prep = connect.prepareStatement(sql)) {
            prep.setString(1, username);
            prep.setString(2, hashPassword);
            prep.setString(3, email);
            prep.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyUser(String username, String providedPassword) {
        String sql = "SELECT hashed_password FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashed = rs.getString("hashed_password");
                return BCrypt.checkpw(providedPassword, hashed);
            } else {
                return false;
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println("Error verifying user: " + e.getMessage());
            return false;
        }
    }

    public Optional<Integer> getUserId(String username) {
        String sql = "SELECT id FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(rs.getInt("id"));
            return Optional.empty();
        } catch (SQLException e) {
            System.out.println("Error fetching user ID: " + e.getMessage());
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
