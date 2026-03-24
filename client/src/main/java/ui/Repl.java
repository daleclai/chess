package ui;

import javax.script.ScriptException;
import java.util.Scanner;

public class Repl {
    private final PreloginClient preloginClient;
    private State state = State.PRELOGIN;

    public enum State {
        PRELOGIN, POSTLOGIN, QUIT
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
            }
        }

        System.out.println("Goodbye!");
    }

    public void setState(State state) {
        this.state = state;
    }

    private void printPrompt() {
        String label = state == State.PRELOGIN ? "[LOGGED OUT]" : "[LOGGED IN]";
        System.out.print("\n" + label + " >>> ");
    }
}