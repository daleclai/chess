package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.UserService;
import service.request.LoginRequest;
import service.result.AuthData;

import java.util.Map;

public class LoginHandler implements Handler {

    private final UserService service;
    private final Gson gson = new Gson();

    public LoginHandler(UserService service) {
        this.service = service;
    }

    @Override
    public void handle(Context ctx) throws DataAccessException {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            if (request.getUsername() == null || request.getPassword() == null) {
                ctx.status(400);
                ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
                return;
            }
            AuthData result = service.login(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized")) {
                ctx.status(401);
            }
            else if (e.getMessage().contains("bad request")) {
                ctx.status(400);
            } else {
                throw e;
            }
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        }
    }
}