package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;


import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlDataAccess implements DataAccess {
    private final Gson gson = new Gson();
    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(256) PRIMARY KEY,
                hashed_password VARCHAR(256) NOT NULL,
                email VARCHAR(256) NOT NULL
                )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(256) PRIMARY KEY,
                username VARCHAR(256) NOT NULL
                )
            """,

            """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(256),
                blackUsername VARCHAR(256),
                gameName VARCHAR(256) NOT NULL,
                game_state TEXT NOT NULL
                )
            """
    };

    public MySqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connect = DatabaseManager.getConnection()) {
            for (String statement: CREATE_STATEMENTS) {
                try (PreparedStatement prep = connect.prepareStatement(statement)) {
                    prep.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String[] tables = {"auth", "game", "user"};
        try (Connection connect = DatabaseManager.getConnection()) {
            for (String table:tables) {
                try (PreparedStatement prep = connect.prepareStatement("DELETE FROM " + table)) {
                    prep.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }
    }

    //Users
    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO user (username, hashed_password, email) VALUES (?, ?, ?)";
        String hashed = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try (Connection connect = DatabaseManager.getConnection();
            PreparedStatement prep = connect.prepareStatement(sql)) {
            prep.setString(1, user.username());
            prep.setString(2, hashed);
            prep.setString(3, user.email());
            prep.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Error: already taken");
            }
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, hashed_password, email FROM user WHERE username = ?";
        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement prep = connect.prepareStatement(sql)) {
            prep.setString(1, username);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("hashed_password"),
                            rs.getString("email")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }

    }


    //authentication
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (Connection connect = DatabaseManager.getConnection();
            PreparedStatement prep = connect.prepareStatement(sql)) {
            prep.setString(1, auth.token());
            prep.setString(2, auth.username());
            prep.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (Connection connect = DatabaseManager.getConnection();
            PreparedStatement prep = connect.prepareStatement(sql)) {
            prep.setString(1, authToken);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";
        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement prep = connect.prepareStatement(sql)) {
            prep.setString(1, authToken);
            prep.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }
    }


    //Games
    @Override
    public int createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO game (gameName, game_state) VALUES (?, ?)";
        String json = gson.toJson(new ChessGame());
        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement prep = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prep.setString(1, gameName);
            prep.setString(2, json);
            prep.executeUpdate();
            try (ResultSet rs = prep.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }
        throw new DataAccessException("Error: game ID not generated");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game_state FROM game WHERE gameID = ?";
        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement prep = connect.prepareStatement(sql)) {
            prep.setInt(1, gameID);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    return readGame(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }
    }


    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game_state FROM game";
        Collection<GameData> games = new ArrayList<>();
        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement prep = connect.prepareStatement(sql);
             ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {
                games.add(readGame(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }
        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
             UPDATE game
             SET whiteUsername = ?, blackUsername = ?, gameName = ?, game_state = ?
             WHERE gameID = ?
             """;
        String json = gson.toJson(game.game());
        try (Connection connect = DatabaseManager.getConnection();
             PreparedStatement prep = connect.prepareStatement(sql)) {
            prep.setString(1, game.whiteUsername());
            prep.setString(2, game.blackUsername());
            prep.setString(3, game.gameName());
            prep.setString(4, json);
            prep.setInt(5, game.gameID());
            prep.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage(), e);
        }

    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int id = rs.getInt("gameID");
        String white = rs.getString("whiteUsername");
        String black = rs.getString("blackUsername");
        String name = rs.getString("gameName");
        ChessGame game = gson.fromJson(rs.getString("game_state"), ChessGame.class);
        return new GameData(id, white, black, name, game);
    }
}
