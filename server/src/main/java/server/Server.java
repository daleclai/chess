package server;

import io.javalin.Javalin;
import server.handlers.RegisterHandler;
import dataaccess.MemoryDataAccess;

public class Server {

    private final Javalin javalin;
    private final MemoryDataAccess dataAccess;

    public Server() {
        dataAccess = new MemoryDataAccess();
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.post("/user", new RegisterHandler(dataAccess));
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
}