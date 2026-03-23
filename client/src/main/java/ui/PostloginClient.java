package ui;

import chess.*;
import java.util.*;

public class PostloginClient {
    private final ServerFacade facade;
    private final Repl repl;
    private final String authToken;
    private List<Map> lastGameList = new ArrayList<>();

    public PostloginClient(ServerFacade facade, Repl repl, String authToken) {
        this.facade = facade;
        this.repl = repl;
        this.authToken = authToken;
    }

    public void eval(String input) {
        String[] tokens = input.split("\\s+");
        String cmd = tokens[0].toLowerCase();
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            switch (cmd) {
                case "help" -> printHelp();
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void logout() throws Exception {
        facade.logout(authToken);
        System.out.println("Logged out.");
        repl.setState(Repl.State.PRELOGIN);
    }

    private void createGame(String[] params) throws Exception {
        if (params.length < 1) {
            System.out.println("Usage: create <game name>");
            return;
        }
        String gameName = String.join(" ", params);
        facade.createGame(authToken, gameName);
        System.out.println("Created game: " + gameName);
    }

    private void listGames() throws Exception {
        Map result = (Map) facade.listGames(authToken);
        List<Map> games = (List<Map>) result.get("games");
        lastGameList = games != null ? games : new ArrayList<>();

        if (lastGameList.isEmpty()) {
            System.out.println("No games available.");
            return;
        }

        for (int i = 0; i < lastGameList.size(); i++) {
            Map game = lastGameList.get(i);
            String name = (String) game.get("gameName");
            String white = game.get("whiteUsername") != null ? (String) game.get("whiteUsername") : "open";
            String black = game.get("blackUsername") != null ? (String) game.get("blackUsername") : "open";
            System.out.printf("%d. %s  [white: %s] [black: %s]%n", i + 1, name, white, black);
        }
    }

    private void playGame(String[] params) throws Exception {
        if (params.length < 2) {
            System.out.println("Usage: play <game number> <WHITE|BLACK>");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(params[0]) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid game number.");
            return;
        }

        if (index < 0 || index >= lastGameList.size()) {
            System.out.println("Invalid game number. Use 'list' to see available games.");
            return;
        }

        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Color must be WHITE or BLACK.");
            return;
        }

        int gameID = ((Double) lastGameList.get(index).get("gameID")).intValue();
        facade.joinGame(authToken, gameID, color);

        ChessGame.TeamColor perspective = color.equals("WHITE")
                ? ChessGame.TeamColor.WHITE
                : ChessGame.TeamColor.BLACK;

        ChessBoard board = new ChessBoard();
        board.resetBoard();
        BoardDrawer.draw(board, perspective);
    }

    private void observeGame(String[] params) throws Exception {
        if (params.length < 1) {
            System.out.println("Usage: observe <game number>");
            return;
        }

        int index;
        try {
            index = Integer.parseInt(params[0]) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid game number.");
            return;
        }

        if (index < 0 || index >= lastGameList.size()) {
            System.out.println("Invalid game number. Use 'list' to see available games.");
            return;
        }

        ChessBoard board = new ChessBoard();
        board.resetBoard();
        BoardDrawer.draw(board, ChessGame.TeamColor.WHITE);
    }

    private void printHelp() {
        System.out.println("""
                  list                         - list all games
                  create <name>                - create a new game
                  play <number> <WHITE|BLACK>  - join a game
                  observe <number>             - observe a game
                  logout                       - logout
                  help                         - show this menu
                """);
    }
}