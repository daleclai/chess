package server.handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.GameService;
import service.result.ListGameResult;

import java.util.Map;

public class ListGamesHandler implements Handler {

    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");

        try {
            ListGameResult result = gameService.listGames(authToken);
            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e) {
            ctx.status(401); // unauthorized
            ctx.json(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(400); // bad request
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}