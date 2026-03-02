package service.result;

public class CreateGameResult {
    private final int gameID;

    public CreateGameResult(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}