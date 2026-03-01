package dataaccess;
import model.*;
import java.util.*;

public class MemoryDataAccess implements DataAccess{

    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> tokens = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();

    private int nextGameID = 1;

    //clear
    @Override
    public void clear() {
        users.clear();
        tokens.clear();
        games.clear();
        nextGameID = 1;
    }

    //users
    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    // authenticate
    @Override
    public void createAuth(AuthData auth) {
        tokens.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return tokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        tokens.remove(authToken);
    }

    //games
    public int createGame(String gameName) {
        int id = nextGameID++;
        GameData game = new GameData(id, null, null, gameName, null);
        games.put(id, game);
        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found");
        }
        games.put(game.gameID(), game);
    }


}
