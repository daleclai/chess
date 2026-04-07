package server.websocket;

import chess.ChessGame;
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
                    MoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    handleMakeMove(ctx, moveCommand);
                }
                case LEAVE -> handleLeave(ctx, command);
                case RESIGN -> handleResign(ctx, command);
            }
        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    //handlers

    private void handleConnect(WsMessageContext ctx, UserGameCommand command) throws DataAccessException, IOException {
        AuthData auth = dataAccess.getAuth(command.getAuthToken());
        if (auth == null) {
            sendError(ctx, "Error: unauthorized");
            return;
        }

        GameData game = dataAccess.getGame(command.getGameID());
        if (game==null) {
            sendError(ctx, "Error: game not found");
            return;
        }

        connections.add(command.getGameID(), ctx.session());

        //send load game to client
        String loadMsg = gson.toJson(new LoadGame(game));
        connections.sendToRoot(ctx.session(), loadMsg);

        String username = auth.username();
        String role;
        if (username.equals(game.whiteUsername())) {
            role = username + " joined as WHITE";
        } else if (username.equals(game.blackUsername())) {
            role = username + " joined as BLACK";
        } else {
            role = username = " joined as observer";
        }
        String notifyMsg = gson.toJson(new Notification(role));
        connections.broadcast(command.getGameID(), ctx.session, notifyMsg);
    }


    private void handleMakeMove(WsMessageContext ctx, MoveCommand command) throws DataAccessException, IOException {
        AuthData auth = dataAccess.getAuth(command.getAuthToken());
        if (auth == null) {
            sendError(ctx, "Error: unauthorized");
            return;
        }

        GameData gameData = dataAccess.getGame(command.getGameID());
        if (gameData == null) {
            sendError(ctx, "Error: game not found");
        }
        ChessGame game = gameData.game();

        //check if player is in checkmate or stalemate

        if (
                game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                game.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                game.isInStalemate(ChessGame.TeamColor.BLACK)
        ) {
            sendError(ctx, "Error: game is already over");
            return;
        }

        //check whose turn it is
        String username = auth.username();
        ChessGame.TeamColor playerColor = null;
        if (username.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            sendError(ctx, "Error: observers cannot make moves");
            return;
        }

        if (game.getTeamTurn() != playerColor) {
            sendError(ctx, "Error: it is not your turn");
            return;
        }
    }

    private void sendError(WsMessageContext ctx, String s) {
    }
}
