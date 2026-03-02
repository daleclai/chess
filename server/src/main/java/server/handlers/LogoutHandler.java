package server.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.UserService;
import service.request.LogoutRequest;
import service.result.LogoutResult;

public class LogoutHandler implements Handler {
    private final UserService service;

    public LogoutHandler(UserService service) {
        this.service = service;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        LogoutRequest request = ctx.bodyAsClass(LogoutRequest.class);
        LogoutResult result = service.logout(request.getAuthToken());
        ctx.json(result);
        ctx.status(200);
    }
}