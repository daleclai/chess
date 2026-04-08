package ui;

import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notify(ServerMessage message);
}
