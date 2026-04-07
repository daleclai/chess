package websocket.messages;

public class Notification extends ServerMessage {
    private final String notify;
    public Notification(String notify) {
        super(ServerMessage.ServerMessageType.NOTIFICATION);
        this.notify = notify;
    }

    public Object getNotify() {
        return notify;
    }



}
