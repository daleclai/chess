package service.result;

public class CreateGameResult {
    private int gameID;

    public CreateGameResult() {}

    public CreateGameResult(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() { return gameID; }
    public void setGameID(int gameID) { this.gameID = gameID; }
}