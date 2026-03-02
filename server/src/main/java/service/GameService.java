package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import model.AuthData;
import service.request.CreateGameRequest;
import service.request.JoinGameRequest;
import service.result.CreateGameResult;
import service.result.ListGameResult;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // LIST GAMES
    public ListGameResult listGames(String authToken) throws DataAccessException {

        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        Collection<GameData> games = dataAccess.listGames();
        return new ListGameResult(new ArrayList<>(games));
    }

    // CREATE GAME
    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws DataAccessException {

        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (request.getGameName() == null) {
            throw new DataAccessException("Error: bad request");
        }

        int gameID = dataAccess.createGame(request.getGameName());

        return new CreateGameResult(gameID);
    }

    // JOIN GAME
    public void joinGame(String authToken, JoinGameRequest request)
            throws DataAccessException {

        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData game = dataAccess.getGame(request.getGameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String username = auth.username();  // ✅ record style

        if (request.getPlayerColor().equals("WHITE")) {

            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }

            game = new GameData(
                    game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    game.game());

        } else if (request.getPlayerColor().equals("BLACK")) {

            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }

            game = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    game.game());

        } else {
            throw new DataAccessException("Error: bad request");
        }

        dataAccess.updateGame(game);
    }
}