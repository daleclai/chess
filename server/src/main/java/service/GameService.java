package service;

import dataaccess.*;
import model.*;
import java.util.Collection;

public class GameService {
    private final DataAccess dataAccess;
    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // list games
    public ListGames listGames(String authToken) throws DataAccessException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        Collection<GameData> games = dataAccess.listGames();
        return new ListGames(games);
    }

    //create game
    public CreateResult createGame(String authToken, CreateReq request) throws DataAccessException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (request.name() == null) {
            throw new DataAccessException("Error: bad request");
        }
        int ID = dataAccess.createGame(request.name());
        return new CreateResult(ID);
    }

    //join
    public void joinGame(String authToken, JoinReq request) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        GameData game = dataAccess.getGame(request.ID());
        if (game==null) {
            throw new DataAccessException("Error: bad request");
        }
        String username = auth.username();

        if (request.color().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());

        } else if (request.color().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            throw new DataAccessException("Error: bad request");
        }
        dataAccess.updateGame(game);
    }

}
