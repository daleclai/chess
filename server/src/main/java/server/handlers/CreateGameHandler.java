package server.handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.GameService;
import service.request.CreateGameRequest;
import service.result.CreateGameResult;

import java.util.Map;

public class CreateGameHandler implements Handler {

    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");
        CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);

        try {
            CreateGameResult result = gameService.createGame(authToken, request);
            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(400);
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}