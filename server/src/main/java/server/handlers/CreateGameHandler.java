package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.GameService;
import service.request.CreateGameRequest;
import service.result.CreateGameResult;

import java.util.Map;

public class CreateGameHandler implements Handler {

    private final GameService gameService;

    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(Context ctx) {

        String authToken = ctx.header("Authorization");

        try {
            CreateGameRequest request =
                    gson.fromJson(ctx.body(), CreateGameRequest.class);

            if (request == null || request.getGameName() == null) {
                ctx.status(400);
                ctx.result(gson.toJson(
                        Map.of("message", "Error: bad request")));
                return;
            }

            CreateGameResult result =
                    gameService.createGame(authToken, request);

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (DataAccessException e) {

            String message = e.getMessage();

            if (message.contains("unauthorized")) {
                ctx.status(401);
            } else {
                ctx.status(400);
            }

            ctx.result(gson.toJson(
                    Map.of("message", message)));

        } catch (Exception e) {

            ctx.status(400);
            ctx.result(gson.toJson(
                    Map.of("message", "Error: bad request")));
        }
    }
}