package server;

import io.javalin.Javalin;
import server.handlers.*;
import service.GameService;
import service.UserService;
import dataaccess.MemoryDataAccess;

public class Server {

    private final Javalin javalin;
    private final MemoryDataAccess dataAccess;

    public Server() {
        dataAccess = new MemoryDataAccess();

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
            dataAccess.clear();
            ctx.status(200);
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