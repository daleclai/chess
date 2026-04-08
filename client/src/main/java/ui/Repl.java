package ui;

import javax.script.ScriptException;
import java.util.Scanner;

public class Repl {
    private final PreloginClient preloginClient;
    private State state = State.PRELOGIN;
    private GamePlay gamePlay;

    public enum State {
        PRELOGIN, POSTLOGIN, GAMEPLAY, QUIT
    }

    public Repl(int port) {
        ServerFacade facade = new ServerFacade(port);
        this.preloginClient = new PreloginClient(facade, this);
    }

    public void run() throws ScriptException {
        System.out.println("Welcome to Chess! Type 'help' to get started.");
        Scanner scanner = new Scanner(System.in);

        while (state != State.QUIT) {
            printPrompt();
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (state == State.PRELOGIN) {
                preloginClient.eval(line);
            } else if (state == State.POSTLOGIN) {
                preloginClient.getPostloginClient().eval(line);
            } else if (state == State.GAMEPLAY) {
                gamePlay.eval(line);
            }
        }

        System.out.println("Goodbye!");
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setGamePlay(GamePlay client) {
        this.gamePlay = client;
    }

    void printPrompt() {
        String label = switch (state) {
            case PRELOGIN -> "[LOGGED OUT]";
            case POSTLOGIN -> "[LOGGED IN]";
            case GAMEPLAY -> "[IN GAME]";
            default -> "";
        };

        System.out.print("\n" + label + " >>> ");
    }
}