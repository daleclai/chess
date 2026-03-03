package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.GameService;
import service.result.ListGameResult;

import java.util.Map;

public class ListGamesHandler implements Handler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");

        try {
            ListGameResult result =
                    gameService.listGames(authToken);

            ctx.status(200);

            ctx.result(gson.toJson(result));

        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        }
    }
}