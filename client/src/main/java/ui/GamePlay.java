package ui;

import chess.*;
import com.google.gson.Gson;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

public class GamePlay implements ServerMessageObserver {

    private final WSFacade ws;
    private final String authToken;
    private final int gameID;
    private final Repl repl;
    private final ChessGame.TeamColor color;
    private ChessGame currentGame;
    private final Gson gson = new Gson();

    public GamePlay(int port, String authToken, int gameID,
                          ChessGame.TeamColor playerColor, Repl repl) throws Exception {
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = playerColor;
        this.repl = repl;
        this.ws = new WSFacade(port, this);
        ws.sendConnect(authToken, gameID);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGame loadGame = gson.fromJson(gson.toJson(message), LoadGame.class);
                currentGame = gson.fromJson(gson.toJson(loadGame.getGame()), ChessGame.class);
                ChessGame.TeamColor perspective = color != null
                        ? color : ChessGame.TeamColor.WHITE;
                BoardDrawer.draw(currentGame.getBoard(), perspective);
                printPrompt();
            }
            case NOTIFICATION -> {
                Notification notification = gson.fromJson(gson.toJson(message), Notification.class);
                System.out.println("\n" + notification.getMessage());
                printPrompt();
            }
            case ERROR -> {
                ErrorMessage error = gson.fromJson(gson.toJson(message), ErrorMessage.class);
                System.out.println("\nError: " + error.getErrorMessage());
                printPrompt();
            }
        }
    }
    public void eval(String input) {
        String[] tokens = input.split("\\s+");
        String cmd = tokens[0].toLowerCase();
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            switch (cmd) {
                case "help" -> printHelp();
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private void redraw() {
        if (currentGame == null) {
            System.out.println("No game loaded yet.");
            return;
        }
        ChessGame.TeamColor perspective = color != null
                ? color : ChessGame.TeamColor.WHITE;
        BoardDrawer.draw(currentGame.getBoard(), perspective);
    }

    private void leave() throws Exception {
        ws.leave(authToken, gameID);
        ws.close();
        repl.setState(Repl.State.POSTLOGIN);
    }

    private void makeMove(String[] params) throws Exception {
        if (params.length < 1) {
            System.out.println("Usage: move <e2e4> or move <e7e8q> for promotion");
            return;
        }
        ChessMove move = parseMove(params[0]);
        if (move == null) {
            System.out.println("Invalid move format. Use format like e2e4 or e7e8q for promotion.");
            return;
        }
        ws.makeMove(authToken, gameID, move);
    }

    private void resign() throws Exception {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        Scanner scanner = new Scanner(System.in);
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes") || confirm.equals("y")) {
            ws.resign(authToken, gameID);
        } else {
            System.out.println("Resign cancelled.");
        }
    }

    private void highlight(String[] params) {
        if (params.length < 1) {
            System.out.println("Usage: highlight <e2>");
            return;
        }
        if (currentGame == null) {
            System.out.println("No game loaded yet.");
            return;
        }
        ChessPosition position = parsePosition(params[0]);
        if (position == null) {
            System.out.println("Invalid position. Use format like e2.");
            return;
        }
        Collection<ChessMove> moves = currentGame.validMoves(position);
        ChessGame.TeamColor perspective = color != null
                ? color : ChessGame.TeamColor.WHITE;
        BoardDrawer.drawHighlighted(currentGame.getBoard(), perspective, position, moves);
    }

    private ChessMove parseMove(String input) {
        if (input.length() < 4) {
            return null;
        }
        ChessPosition start = parsePosition(input.substring(0, 2));
        ChessPosition end = parsePosition(input.substring(2, 4));
        if (start == null || end == null) {
            return null;
        }

        ChessPiece.PieceType promotion = null;
        if (input.length() == 5) {
            promotion = switch (input.charAt(4)) {
                case 'q' -> ChessPiece.PieceType.QUEEN;
                case 'r' -> ChessPiece.PieceType.ROOK;
                case 'b' -> ChessPiece.PieceType.BISHOP;
                case 'n' -> ChessPiece.PieceType.KNIGHT;
                default -> null;
            };
        }
        return new ChessMove(start, end, promotion);
    }

    private ChessPosition parsePosition(String input) {
        if (input.length() < 2) {
            return null;
        }
        int col = input.charAt(0) - 'a' + 1;
        int row;
        try {
            row = Integer.parseInt(String.valueOf(input.charAt(1)));
        } catch (NumberFormatException e) {
            return null;
        }
        if (col < 1 || col > 8 || row < 1 || row > 8) {
            return null;
        }
        return new ChessPosition(row, col);
    }

    private void printHelp() {
        System.out.println("""
                  redraw                - redraw the chess board
                  move <e2e4>           - make a move (add q/r/b/n for promotion e.g. e7e8q)
                  highlight <e2>        - highlight legal moves for a piece
                  resign                - forfeit the game
                  leave                 - leave the game
                  help                  - show this menu
                """);
    }
    private void printPrompt() {
        System.out.print("\n[IN GAME] >>> ");
    }
}