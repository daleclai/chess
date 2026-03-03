package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.UserService;

import java.util.Map;

public class LogoutHandler implements Handler {

    private final UserService service;

    private final Gson gson = new Gson();

    public LogoutHandler(UserService service) {
        this.service = service;
    }

    @Override
    public void handle(Context ctx) {
        String authToken = ctx.header("Authorization");

        try {
            var result = service.logout(authToken);

            ctx.status(200);

            ctx.result(gson.toJson(result));

        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        }
    }
}