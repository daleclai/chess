package service.result;

import java.util.List;
import model.GameData;

public class ListGameResult {
    private List<GameData> games;

    public ListGameResult() {}

    public ListGameResult(List<GameData> games) {
        this.games = games;
    }

    public List<GameData> getGames() { return games; }
    public void setGames(List<GameData> games) { this.games = games; }
}