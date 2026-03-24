package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    public static void draw(ChessBoard board, ChessGame.TeamColor teamColor) {
        boolean white = teamColor == ChessGame.TeamColor.WHITE;

        String[] labels = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        String border = SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD;
        String reset = RESET_BG_COLOR + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT;

        System.out.print(border + "  ");
        for (int c = 0; c<8; c++) {
            int col = white ? c : 7 - c;
            System.out.print(labels[col]);
        }
        System.out.println("  " + reset);

        for (int r=0; r<8; r++) {
            int row = white ? 7 - r : r;
            System.out.print(border + " " + (row + 1) + " " + reset);

            for (int c = 0; c < 8; c++) {
                int col = white ? c : 7 - c;
                boolean lightSqare = (row + col) % 2 != 0;
                String bg = lightSqare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_GREEN;

                ChessPosition position = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = board.getPiece(position);
                System.out.print(bg + getPieceString(piece) + reset);
            }
            System.out.println(border + " " + (row + 1) + " " + reset);

        }
        System.out.print(border + "  ");
        for (int c = 0; c < 8; c++) {
            int col = white ? c : 7 - c;
            System.out.print(labels[col]);
        }
        System.out.println("  " + reset);
        }


    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        String color = isWhite ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
        return color + switch (piece.getPieceType()) {
            case KING -> isWhite ? WHITE_KING  : BLACK_KING;
            case QUEEN -> isWhite ? WHITE_QUEEN  : BLACK_QUEEN;
            case BISHOP -> isWhite ? WHITE_BISHOP  : BLACK_BISHOP;
            case KNIGHT -> isWhite ? WHITE_KNIGHT  : BLACK_KNIGHT;
            case ROOK -> isWhite ? WHITE_ROOK  : BLACK_ROOK;
            case PAWN -> isWhite ? WHITE_PAWN  : BLACK_PAWN;
        };
    }
}
