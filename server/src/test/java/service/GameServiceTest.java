package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.CreateGameRequest;
import service.request.JoinGameRequest;
import service.request.RegRequest;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private DataAccess dao;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        dao = new MemoryDataAccess();
        userService = new UserService(dao);
        gameService = new GameService(dao);
    }

    // create

    @Test
    void createGamePositive() throws DataAccessException {

        var reg = userService.register(
                new RegRequest("bob", "pass", "email"));

        var request = new CreateGameRequest("MyGame");

        var result = gameService.createGame(
                reg.getAuthToken(), request);

        assertNotNull(result);
        assertTrue(result.getGameID() > 0);
    }

    @Test
    void createGameNegativeBadToken() {

        var request = new CreateGameRequest("Game");

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame("badToken", request);
        });
    }

    // list

    @Test
    void listGamesPositive() throws DataAccessException {

        var reg = userService.register(
                new RegRequest("alice", "pass", "email"));

        gameService.createGame(
                reg.getAuthToken(),
                new CreateGameRequest("Game1"));

        var result = gameService.listGames(reg.getAuthToken());

        assertNotNull(result);
        assertFalse(result.games().isEmpty());
    }

    @Test
    void listGamesNegativeBadToken() {

        assertThrows(DataAccessException.class, () -> {
            gameService.listGames("badToken");
        });
    }

    //join

    @Test
    void joinGamePositive() throws DataAccessException {

        var reg = userService.register(
                new RegRequest("sam", "pass", "email"));

        var createResult = gameService.createGame(
                reg.getAuthToken(),
                new CreateGameRequest("JoinableGame"));

        var joinRequest = new JoinGameRequest(
                "WHITE",
                createResult.getGameID());

        assertDoesNotThrow(() ->
                gameService.joinGame(reg.getAuthToken(), joinRequest));
    }

    @Test
    void joinGameNegativeColorTaken() throws DataAccessException {

        var reg1 = userService.register(
                new RegRequest("p1", "pass", "email"));

        var reg2 = userService.register(
                new RegRequest("p2", "pass", "email"));

        var createResult = gameService.createGame(
                reg1.getAuthToken(),
                new CreateGameRequest("Game"));

        var joinRequest1 = new JoinGameRequest(
                "WHITE",
                createResult.getGameID());

        var joinRequest2 = new JoinGameRequest(
                "WHITE",
                createResult.getGameID());

        gameService.joinGame(reg1.getAuthToken(), joinRequest1);

        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(reg2.getAuthToken(), joinRequest2);
        });
    }
}