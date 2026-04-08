package ui;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WSFacade extends WebSocketClient {

    private final Gson gson = new Gson();
    private final ServerMessageObserver observer;
    public WSFacade(int port, ServerMessageObserver observer) throws Exception {
        super(new URI("ws://localhost:" + port + "/ws"));
        this.observer = observer;
        connectBlocking();
    }
    @Override
    public void onOpen(ServerHandshake handshake) {
    }
    @Override
    public void onMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        observer.notify(serverMessage);
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
    }
    @Override
    public void onError(Exception e) {
        System.err.println("WebSocket error: " + e.getMessage());
    }
    public void sendConnect(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT, authToken, gameID);
        send(gson.toJson(command));
    }
    public void makeMove(String authToken, int gameID, chess.ChessMove move) throws Exception {
        MoveCommand command = new MoveCommand(authToken, gameID, move);
        send(gson.toJson(command));
    }
    public void leave(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE, authToken, gameID);
        send(gson.toJson(command));
    }
    public void resign(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN, authToken, gameID);
        send(gson.toJson(command));
    }
}