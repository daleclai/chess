package client;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clear() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    //register

    @Test
    void registerPositive() throws Exception {
        var result = (Map) facade.register("alice", "password", "alice@email.com");
        assertNotNull(result.get("authToken"));
        assertTrue(result.get("authToken").toString().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register("alice", "password", "alice@email.com");
        assertThrows(Exception.class, () ->
                facade.register("alice", "password", "alice@email.com")); // duplicate username
    }

    //login


    @Test
    void loginPositive() throws Exception {
        facade.register("bob", "password", "bob@email.com");
        var result = (Map) facade.login("bob", "password");
        assertNotNull(result.get("authToken"));
    }

    @Test
    void loginNegative() throws Exception {
        assertThrows(Exception.class, () ->
                facade.login("nobody", "wrongpassword")); // user doesn't exist
    }

    //logout

    @Test
    void logoutPositive() throws Exception {
        var result = (Map) facade.register("carol", "password", "carol@email.com");
        String token = (String) result.get("authToken");
        assertDoesNotThrow(() -> facade.logout(token));
    }

    @Test
    void logoutNegative() throws Exception {
        assertThrows(Exception.class, () ->
                facade.logout("fake-token-that-does-not-exist"));
    }

    //createGame

    @Test
    void createGamePositive() throws Exception {
        var result = (Map) facade.register("dave", "password", "dave@email.com");
        String token = (String) result.get("authToken");
        var game = (Map) facade.createGame(token, "My Game");
        assertNotNull(game.get("gameID"));
    }

    @Test
    void createGameNegative() throws Exception {
        assertThrows(Exception.class, () ->
                facade.createGame("bad-token", "My Game")); // no auth
    }

    //listGames

    @Test
    void listGamesPositive() throws Exception {
        var result = (Map) facade.register("eve", "password", "eve@email.com");
        String token = (String) result.get("authToken");
        facade.createGame(token, "Game 1");
        facade.createGame(token, "Game 2");
        var response = (Map) facade.listGames(token);
        var games = (List) response.get("games");
        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegative() throws Exception {
        assertThrows(Exception.class, () ->
                facade.listGames("bad-token")); // no auth
    }

    //joinGame

    @Test
    void joinGamePositive() throws Exception {
        var result = (Map) facade.register("frank", "password", "frank@email.com");
        String token = (String) result.get("authToken");
        var game = (Map) facade.createGame(token, "Frank's Game");
        int gameID = ((Double) game.get("gameID")).intValue();
        assertDoesNotThrow(() -> facade.joinGame(token, gameID, "WHITE"));
    }

    @Test
    void joinGameNegative() throws Exception {
        var result = (Map) facade.register("grace", "password", "grace@email.com");
        String token = (String) result.get("authToken");
        var game = (Map) facade.createGame(token, "Grace's Game");
        int gameID = ((Double) game.get("gameID")).intValue();
        facade.joinGame(token, gameID, "WHITE");
        var result2 = (Map) facade.register("henry", "password", "henry@email.com");
        String token2 = (String) result2.get("authToken");
        assertThrows(Exception.class, () ->
                facade.joinGame(token2, gameID, "WHITE")); // already taken
    }
}