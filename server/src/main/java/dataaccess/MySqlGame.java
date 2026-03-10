package dataaccess;

import java.sql.*;
import java.util.Optional;

import chess.ChessGame;
import com.google.gson.Gson;

public class MySqlGame {

    private final Gson gson = new Gson();

    public boolean createGame(int player1Id, int player2Id, ChessGame game) {
        String sql = "INSERT INTO `game` (player1_id, player2_id, game_state) VALUES (?, ?, ?)";
        String jsonState = gson.toJson(game);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, player1Id);
            ps.setInt(2, player2Id);
            ps.setString(3, jsonState);
            ps.executeUpdate();
            return true;
        } catch (SQLException | DataAccessException e) {
            System.out.println("Error creating game: " + e.getMessage());
            return false;
        }
    }

    public Optional<ChessGame> getGame(int gameId) {
        String sql = "SELECT game_state FROM `game` WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String jsonState = rs.getString("game_state");
                ChessGame game = gson.fromJson(jsonState, ChessGame.class);
                return Optional.of(game);
            }
            return Optional.empty();
        } catch (SQLException e) {
            System.out.println("Error fetching game: " + e.getMessage());
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateGame(int gameId, ChessGame game) {
        String sql = "UPDATE `game` SET game_state = ? WHERE id = ?";
        String jsonState = gson.toJson(game);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, jsonState);
            ps.setInt(2, gameId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating game: " + e.getMessage());
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteGame(int gameId) {
        String sql = "DELETE FROM `game` WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting game: " + e.getMessage());
            return false;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}