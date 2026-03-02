package server.handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.GameService;
import service.request.JoinGameRequest;

import java.util.Map;

public class JoinGameHandler implements Handler {

    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");
        JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);

        try {
            gameService.joinGame(authToken, request);
            ctx.status(200);
            ctx.json(Map.of("message", "Joined game successfully"));
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.json(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            ctx.status(400);
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}