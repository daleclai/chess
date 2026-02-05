package chess.moves_calc;

import chess.*;
import java.util.HashSet;

public class pawn_calc {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        HashSet<ChessMove> moves = new HashSet<>();

        int x = currentPosition.getColumn();
        int y = currentPosition.getRow();

        ChessGame.TeamColor color = board.getPieceTeam(currentPosition);
        int dir = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;

        boolean promote =
                (color == ChessGame.TeamColor.WHITE && y == 7) ||
                        (color == ChessGame.TeamColor.BLACK && y == 2);

        ChessPiece.PieceType[] promoPieces = promote
                ? new ChessPiece.PieceType[]{
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN
        }
                : new ChessPiece.PieceType[]{null};

        for (ChessPiece.PieceType promo : promoPieces) {

            ChessPosition forward = new ChessPosition(y + dir, x);
            if (MovesCalc.isValidSquare(forward) && board.getPiece(forward) == null) {
                moves.add(new ChessMove(currentPosition, forward, promo));
            }

            //Left atk//
            ChessPosition leftAtk = new ChessPosition(y + dir, x - 1);
            if (MovesCalc.isValidSquare(leftAtk) &&
                    board.getPiece(leftAtk) != null &&
                    board.getPieceTeam(leftAtk) != color) {

                moves.add(new ChessMove(currentPosition, leftAtk, promo));
            }

            //right atk//
            ChessPosition rightAtk = new ChessPosition(y + dir, x + 1);
            if (MovesCalc.isValidSquare(rightAtk) &&
                    board.getPiece(rightAtk) != null &&
                    board.getPieceTeam(rightAtk) != color) {

                moves.add(new ChessMove(currentPosition, rightAtk, promo));
            }

            // double forward //
            ChessPosition oneForward = new ChessPosition(y + dir, x);
            ChessPosition doubleForward = new ChessPosition(y + dir * 2, x);

            if (MovesCalc.isValidSquare(doubleForward) &&
                    ((color == ChessGame.TeamColor.WHITE && y == 2) ||
                            (color == ChessGame.TeamColor.BLACK && y == 7)) &&
                    board.getPiece(oneForward) == null &&
                    board.getPiece(doubleForward) == null) {

                moves.add(new ChessMove(currentPosition, doubleForward, null));
            }
        }
        return moves;
    }
}