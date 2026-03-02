package server.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class ClearHandler implements Handler {

    private final DataAccess dataAccess;

    public ClearHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        dataAccess.clear();
        ctx.status(200);
        ctx.result("Database cleared");
    }
}