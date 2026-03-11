package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.GameService;
import service.request.JoinGameRequest;

import java.util.Map;

public class JoinGameHandler implements Handler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void handle(Context ctx) throws DataAccessException {
        String authToken = ctx.header("Authorization");
        try {
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            if (request == null || request.getGameID() <= 0 || request.getPlayerColor() == null) {
                ctx.status(400);
                ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
                return;
            }
            gameService.joinGame(authToken, request);
            ctx.status(200);
            ctx.result(gson.toJson(Map.of("message", "Joined game successfully")));
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                ctx.status(401);
            } else if (e.getMessage().contains("already taken")) {
                ctx.status(403);
            } else if (e.getMessage().contains("bad request")) {
                ctx.status(400);
            } else {
                throw e;
            }
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        }
    }
}