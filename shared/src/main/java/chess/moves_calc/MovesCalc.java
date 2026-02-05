package chess.moves_calc;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class MovesCalc {

    static boolean isValidSquare(ChessPosition position) {
        return (position.getRow()>=1 && position.getRow()<=8) && (position.getColumn()>=1 && position.getColumn()<=8);
    }

    static HashSet<ChessMove> makeStaticMoves(ChessPosition currentPosition, int[][] rel_moves, ChessBoard board) {
        HashSet<ChessMove> moves=new HashSet<>();
        int current_x = currentPosition.getColumn();
        int current_y = currentPosition.getRow();
        ChessGame.TeamColor pieceColor = board.getPieceTeam(currentPosition);

        for (int[] rel_move : rel_moves) {
            ChessPosition possible_pos= new ChessPosition(current_y + rel_move[1], current_x + rel_move[0]);
            if (!MovesCalc.isValidSquare(possible_pos)) continue;
            if (board.getPiece(possible_pos) == null || board.getPieceTeam(possible_pos) != pieceColor) {
                moves.add(new ChessMove(currentPosition, possible_pos, null));
            }
        }
        return moves;
    }

    static HashSet<ChessMove> makeDirection(ChessBoard board, ChessPosition currentPosition, int[][] moveDirections, int current_y, int current_x, ChessGame.TeamColor pieceColor) {
        HashSet<ChessMove> moves= new HashSet<>();
        for (int[] direction : moveDirections) {
            boolean obstructed = false;
            int i=1;
            while (!obstructed) {
                ChessPosition possiblePosition = new ChessPosition(current_y + direction[1]*i, current_x + direction[0]*i);
                if (!MovesCalc.isValidSquare(possiblePosition)) {
                    obstructed = true;
                }
                else if (board.getPiece(possiblePosition) == null) {
                    moves.add(new ChessMove(currentPosition, possiblePosition, null));
                }
                else if (board.getPieceTeam(possiblePosition) != pieceColor) {
                    moves.add(new ChessMove(currentPosition, possiblePosition, null));
                    obstructed = true;
                }
                else if (board.getPieceTeam(possiblePosition) == pieceColor) {
                    obstructed = true;
                }
                i++;
            }
        }
        return moves;
    }
}
