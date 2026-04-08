package websocket.messages;

public class Notification extends ServerMessage {
    private final String message;
    public Notification(String message) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }



}
