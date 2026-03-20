package ui;

import java.util.Arrays;
import java.util.Map;

public class PreloginClient {
    private final ServerFacade facade;
    private final Repl repl;
    private PostloginClient postloginClient;

    public PreloginClient(ServerFacade facade, Repl repl) {
        this.facade = facade;
        this.repl = repl;
    }

    public void eval(String input) {
        String[] tokens = input.split("\\s+");
        String cmd = tokens[0].toLowerCase();
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            switch (cmd) {
                case "help" -> printHelp();
                case "quit" -> repl.setState(Repl.State.QUIT);
                case "login" -> login(params);
                case "register" -> register(params);
                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void login(String[] params) throws Exception {
        if (params.length < 2) {
            System.out.println("Usage: login <username> <password>");
            return;
        }
        Map result = (Map) facade.login(params[0], params[1]);
        String authToken = (String) result.get("authToken");
        System.out.println("Logged in as " + params[0]);
        postloginClient = new PostloginClient(facade, repl, authToken);
        repl.setState(Repl.State.POSTLOGIN);
    }

    private void register(String[] params) throws Exception {
        if (params.length < 3) {
            System.out.println("Usage: register <username> <password> <email>");
            return;
        }
        Map result = (Map) facade.register(params[0], params[1], params[2]);
        String authToken = (String) result.get("authToken");
        System.out.println("Registered and logged in as " + params[0]);
        postloginClient = new PostloginClient(facade, repl, authToken);
        repl.setState(Repl.State.POSTLOGIN);
    }

    private void printHelp() {
        System.out.println("""
                  register <username> <password> <email> - create an account
                  login <username> <password>            - login to your account
                  quit                                   - exit the program
                  help                                   - show this menu
                """);
    }

    public PostloginClient getPostloginClient() {
        return postloginClient;
    }
}