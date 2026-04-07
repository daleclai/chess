package websocket.messages;

public class LoadGame extends ServerMessage {
    private final Object game;
    public LoadGame(Object game) {
        super(ServerMessage.ServerMessageType.LOAD_GAME);
        this.game = game;
    }
    public Object getGame() {
        return game;
    }
}
