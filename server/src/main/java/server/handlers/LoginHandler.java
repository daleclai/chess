package server.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.UserService;
import service.request.LoginRequest;
import service.result.AuthData;

public class LoginHandler implements Handler {

    private final UserService service;

    public LoginHandler(UserService service) {
        this.service = service;
    }

    @Override
    public void handle(Context ctx) throws Exception {

        LoginRequest request = ctx.bodyAsClass(LoginRequest.class);

        AuthData authData = service.login(request);

        ctx.json(authData);
    }
}