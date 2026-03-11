package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import io.javalin.Javalin;
import server.handlers.*;
import service.GameService;
import service.UserService;


public class Server {

    private final Javalin javalin;
    private final DataAccess dataAccess;

    public Server() {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
        });

        UserService userService = new UserService(dataAccess);
        GameService gameService = new GameService(dataAccess);


        javalin.post("/user", new RegisterHandler(userService));
        javalin.post("/session", new LoginHandler(userService));
        javalin.delete("/session", new LogoutHandler(userService));

        javalin.post("/game", new CreateGameHandler(gameService));
        javalin.put("/game", new JoinGameHandler(gameService));
        javalin.get("/game", new ListGamesHandler(gameService));

        javalin.delete("/db", ctx -> {
            try {
                dataAccess.clear();
                ctx.status(200);
            } catch (DataAccessException e) {
                ctx.status(500);
                ctx.result("{\"message\":\"Error: " + e.getMessage() + "\"}");
            }
        });
        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result("{\"message\":\"Error: " + e.getMessage() + "\"}");
        });

    }

    public int run(int port) {
        javalin.start(port);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run(7000);
    }
}