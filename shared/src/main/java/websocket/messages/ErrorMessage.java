package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;
    public ErrorMessage(String error) {
        super(ServerMessageType.ERROR);
        this.errorMessage = error;
    }
    public Object getErrorMessage() {
        return errorMessage;
    }
}