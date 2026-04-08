package ui;

import chess.*;
import com.google.gson.Gson;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class GamePlay implements ServerMessageObserver {

    private final WSFacade ws;
    private final String authToken;
    private final int gameID;
    private final Repl repl;
    private final ChessGame.TeamColor color;
    private ChessGame currentGame;
    private final Gson gson = new Gson();

    public GamePlay(int port, String authToken, int gameID,
                          ChessGame.TeamColor playerColor, Repl repl) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = playerColor;
        this.repl = repl;
        this.ws = new WSFacade(port, this);
        ws.sendConnect(authToken, gameID);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGame loadGame = gson.fromJson(gson.toJson(message), LoadGame.class);
                currentGame = gson.fromJson(gson.toJson(loadGame.getGame()), ChessGame.class);
                ChessGame.TeamColor perspective = color != null
                        ? color : ChessGame.TeamColor.WHITE;
                BoardDrawer.draw(currentGame.getBoard(), perspective);
                printPrompt();
            }
            case NOTIFICATION -> {
                Notification notification = gson.fromJson(gson.toJson(message), Notification.class);
                System.out.println("\n" + notification.getNotify());
                printPrompt();
            }
            case ERROR -> {
                ErrorMessage error = gson.fromJson(gson.toJson(message), ErrorMessage.class);
                System.out.println("\nError: " + error.getErrorMessage());
                printPrompt();
            }
        }
    }

}