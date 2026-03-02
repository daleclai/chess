package server.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.UserService;
import service.request.RegRequest;
import service.result.RegResult;

public class RegisterHandler implements Handler {

    private final UserService service;

    public RegisterHandler(UserService service) {
        this.service = service;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        RegRequest request = ctx.bodyAsClass(RegRequest.class);

        try {
            RegResult result = service.register(request);
            ctx.status(200);
            ctx.json(result);

        } catch (Exception e) {
            ctx.status(403);
            ctx.json(e.getMessage());
        }
    }
}