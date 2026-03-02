package service.request;

public class JoinGameRequest {
    private String playerColor;
    private int gameID;

    public JoinGameRequest() {}

    public JoinGameRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getPlayerColor() { return playerColor; }
    public int getGameID() { return gameID; }

    public void setPlayerColor(String playerColor) { this.playerColor = playerColor; }
    public void setGameID(int gameID) { this.gameID = gameID; }
}