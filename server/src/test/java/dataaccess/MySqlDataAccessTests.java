package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MySqlDataAccessTests {

    private static MySqlDataAccess dao;

    @BeforeAll
    static void setup() throws DataAccessException {
        dao = new MySqlDataAccess();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        dao.clear();
    }

    // CLEAR
    @Test @Order(1)
    void clearPositive() throws DataAccessException {
        dao.createUser(new UserData("user1", "pass", "e@e.com"));
        dao.clear();
        assertNull(dao.getUser("user1"));
    }

    // CREATE USER
    @Test @Order(2)
    void createUserPositive() throws DataAccessException {
        dao.createUser(new UserData("alice", "pass", "alice@mail.com"));
        assertNotNull(dao.getUser("alice"));
    }

    @Test @Order(3)
    void createUserNegativeDuplicate() throws DataAccessException {
        dao.createUser(new UserData("alice", "pass", "alice@mail.com"));
        assertThrows(DataAccessException.class,
                () -> dao.createUser(new UserData("alice", "other", "other@mail.com")));
    }

    // GET USER
    @Test @Order(4)
    void getUserPositive() throws DataAccessException {
        dao.createUser(new UserData("bob", "pass", "bob@mail.com"));
        assertNotNull(dao.getUser("bob"));
    }

    @Test @Order(5)
    void getUserNegativeNotFound() throws DataAccessException {
        assertNull(dao.getUser("nobody"));
    }

    // CREATE AUTH
    @Test @Order(6)
    void createAuthPositive() throws DataAccessException {
        dao.createAuth(new AuthData("token123", "alice"));
        assertNotNull(dao.getAuth("token123"));
    }

    @Test @Order(7)
    void createAuthNegativeDuplicate() throws DataAccessException {
        dao.createAuth(new AuthData("token123", "alice"));
        assertThrows(DataAccessException.class,
                () -> dao.createAuth(new AuthData("token123", "bob")));
    }

    // GET AUTH
    @Test @Order(8)
    void getAuthPositive() throws DataAccessException {
        dao.createAuth(new AuthData("mytoken", "carol"));
        assertEquals("carol", dao.getAuth("mytoken").username());
    }

    @Test @Order(9)
    void getAuthNegativeNotFound() throws DataAccessException {
        assertNull(dao.getAuth("doesnotexist"));
    }

    // DELETE AUTH
    @Test @Order(10)
    void deleteAuthPositive() throws DataAccessException {
        dao.createAuth(new AuthData("tok", "dave"));
        dao.deleteAuth("tok");
        assertNull(dao.getAuth("tok"));
    }

    @Test @Order(11)
    void deleteAuthNegativeTokenNotPresent() {
        assertDoesNotThrow(() -> dao.deleteAuth("ghost-token"));
    }

    // CREATE GAME
    @Test @Order(12)
    void createGamePositive() throws DataAccessException {
        int id = dao.createGame("MyGame");
        assertTrue(id > 0);
        assertNotNull(dao.getGame(id));
    }

    @Test @Order(13)
    void createGameNegativeNullName() {
        assertThrows(DataAccessException.class, () -> dao.createGame(null));
    }

    // GET GAME
    @Test @Order(14)
    void getGamePositive() throws DataAccessException {
        int id = dao.createGame("Chess!");
        GameData game = dao.getGame(id);
        assertNotNull(game);
        assertEquals("Chess!", game.gameName());
    }

    @Test @Order(15)
    void getGameNegativeNotFound() throws DataAccessException {
        assertNull(dao.getGame(99999));
    }

    // LIST GAMES
    @Test @Order(16)
    void listGamesPositive() throws DataAccessException {
        dao.createGame("G1");
        dao.createGame("G2");
        Collection<GameData> games = dao.listGames();
        assertEquals(2, games.size());
    }

    @Test @Order(17)
    void listGamesNegativeEmpty() throws DataAccessException {
        Collection<GameData> games = dao.listGames();
        assertTrue(games.isEmpty());
    }

    // UPDATE GAME
    @Test @Order(18)
    void updateGamePositive() throws DataAccessException {
        int id = dao.createGame("UpdateMe");
        GameData original = dao.getGame(id);
        ChessGame chessGame = original.game();
        try {
            chessGame.makeMove(new ChessMove(
                    new ChessPosition(2, 5),
                    new ChessPosition(4, 5), null));
        } catch (Exception e) {
            fail("Unexpected invalid move: " + e.getMessage());
        }
        GameData updated = new GameData(id, "white_player", null, "UpdateMe", chessGame);
        dao.updateGame(updated);
        GameData fetched = dao.getGame(id);
        assertEquals("white_player", fetched.whiteUsername());
        assertNotEquals(new ChessGame().getBoard(), fetched.game().getBoard());
    }

    @Test @Order(19)
    void updateGameNegativeNonexistentGame() {
        GameData fake = new GameData(99999, "x", null, "fake", new ChessGame());
        assertDoesNotThrow(() -> dao.updateGame(fake));
    }
}