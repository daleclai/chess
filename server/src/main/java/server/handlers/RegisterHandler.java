package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.UserService;
import service.request.RegRequest;
import service.result.RegResult;

import java.util.Map;

public class RegisterHandler implements Handler {

    private final UserService service;
    private final Gson gson = new Gson();
    public RegisterHandler(UserService service) {
        this.service = service;
    }

    @Override
    public void handle(Context ctx) {
        try {
            RegRequest request =
                    gson.fromJson(ctx.body(), RegRequest.class);

            RegResult result = service.register(request);

            ctx.status(200);

            ctx.result(gson.toJson(result));

        } catch (DataAccessException e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", e.getMessage())));
        } catch (Exception e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        }
    }
}

