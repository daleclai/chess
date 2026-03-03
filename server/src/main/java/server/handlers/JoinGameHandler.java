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
    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");

        try {
            JoinGameRequest request =
                    gson.fromJson(ctx.body(), JoinGameRequest.class);

            gameService.joinGame(authToken, request);

            ctx.status(200);

            ctx.result(gson.toJson(
                    Map.of("message", "Joined game successfully")));

        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        }
    }
}