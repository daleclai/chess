package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.*;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WSHandler extends WsConfig {

    private final DataAccess dataAccess;
    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson= new Gson();

    public WSHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public void init(WsConfig ws) {
        ws.onConnect(this::onConnect);
        ws.onMessage(this::onMessage);
        ws.onClose(this::onClose);
        ws.onError(this::onError);
    }

    private void onConnect(WsConnectContext wsConnectContext) {
    }

    private void onError(WsErrorContext ctx) {
        System.err.println("Websocket error: " + ctx.error().getMessage());
    }

    private void onClose(WsCloseContext ctx) {
        connections.remove(ctx.session());
    }

    private void onMessage(WsMessageContext ctx) {
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(ctx, command);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    handleMakeMove(ctx, moveCommand);
                }
                case LEAVE -> handleLeave(ctx, command);
                case RESIGN -> handleResign(ctx, command);
            }
        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
        }
    }
}
