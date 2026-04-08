package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.*;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

public class WSHandler {

    private final DataAccess dataAccess;
    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson= new Gson();

    public WSHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

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
        connections.remove(ctx.session);
    }

    private void onMessage(WsMessageContext ctx) {
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(ctx, command);
                case MAKE_MOVE -> {
                    MoveCommand moveCommand = gson.fromJson(ctx.message(), MoveCommand.class);
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

        connections.add(command.getGameID(), ctx.session);

        //send load game to client
        String loadMsg = gson.toJson(new LoadGame(game));
        connections.sendToRoot(ctx.session, loadMsg);

        String username = auth.username();
        String role;
        if (username.equals(game.whiteUsername())) {
            role = username + " joined as WHITE";
        } else if (username.equals(game.blackUsername())) {
            role = username + " joined as BLACK";
        } else {
            role = username + " joined as observer";
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
            return;
        }
        ChessGame game = gameData.game();

        if (game.isGameOver()) {
            sendError(ctx, "Error: game is already over");
            return;
        }

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

        //movements
        ChessMove move = command.getMove();
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            sendError(ctx, "Error: invalid move");
            return;
        }

        //save game
        GameData updated = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        );
        dataAccess.updateGame(updated);
        String loadMsg = gson.toJson(new LoadGame(updated));
        connections.broadcastToAll(command.getGameID(), loadMsg);

        //broadcast to everyone
        String moveDesc = move.getStartPosition().toString() + " to " + move.getEndPosition().toString();
        String notifyMsg = gson.toJson(new Notification(username + " moved " + moveDesc));
        connections.broadcast(command.getGameID(), ctx.session, notifyMsg);

        ChessGame.TeamColor opp = (playerColor == ChessGame.TeamColor.WHITE)
                ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String oppName = (opp == ChessGame.TeamColor.WHITE)
                ? gameData.whiteUsername() : gameData.blackUsername();

        if (game.isInCheckmate(opp)) {
            String msg = gson.toJson(new Notification(oppName + " is in Checkmate! Game Over."));
            connections.broadcastToAll(command.getGameID(), msg);
        } else if (game.isInStalemate(opp)) {
            String msg = gson.toJson(new Notification("Stalemate! Game over."));
            connections.broadcastToAll(command.getGameID(), msg);
        } else if (game.isInCheck(opp)){
            String msg = gson.toJson(new Notification(oppName + " is in check!"));
            connections.broadcastToAll(command.getGameID(), msg);
        }

    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command) throws DataAccessException, IOException {
        GameContext gc = validate(ctx, command);
        if (gc == null) {
            return;
        }

        String username = gc.auth().username();
        GameData data = gc.gameData();

        if (username.equals(data.whiteUsername())) {
            GameData updated = new GameData(data.gameID(), null,
                    data.blackUsername(), data.gameName(), data.game());
            dataAccess.updateGame(updated);
        } else if (username.equals(data.blackUsername())) {
            GameData updated = new GameData(data.gameID(), data.whiteUsername(),
                    null, data.gameName(), data.game());
            dataAccess.updateGame(updated);
        }
        String notifyMsg = gson.toJson(new Notification(username + " left the game"));
        connections.broadcast(command.getGameID(), ctx.session, notifyMsg);
        connections.remove(ctx.session);

    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command) throws DataAccessException, IOException {
        GameContext gc = validate(ctx, command);
        if (gc == null) {
            return;
        }

        String username = gc.auth().username();
        GameData gameData = gc.gameData();

        if (!username.equals(gameData.whiteUsername()) &&
        !username.equals(gameData.blackUsername())) {
            sendError(ctx, "Error: observers cannot resign");
            return;
        }

        ChessGame game = gameData.game();

        if (game.isGameOver()) {
            sendError(ctx, "Error: game is already over");
            return;
        }

        game.setGameOver(true);
        GameData updated = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game);
        dataAccess.updateGame(updated);

        String notifyMsg = gson.toJson(new Notification(username + " resigned. Game over."));
        connections.broadcastToAll(command.getGameID(), notifyMsg);
    }

    //helper

    private record GameContext(AuthData auth, GameData gameData) {}

    private GameContext validate(WsMessageContext ctx, UserGameCommand command) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(command.getAuthToken());
        if (auth == null) {
            sendError(ctx, "Error: unauthorized");
            return null;
        }
        GameData gameData = dataAccess.getGame(command.getGameID());
        if (gameData == null) {
            sendError(ctx, "Error: game not found");
            return null;
        }
        return new GameContext(auth, gameData);
    }

    private void sendError(WsMessageContext ctx, String s) {
        try {
            connections.sendToRoot(ctx.session, gson.toJson(new ErrorMessage(s)));
        } catch (IOException e) {
            System.err.println("Failed to send error: " + e.getMessage());
        }
    }
}
