package chess.movescalc;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class MovesCalc {

    static boolean isValidSquare(ChessPosition position) {
        return (position.getRow()>=1 && position.getRow()<=8) && (position.getColumn()>=1 && position.getColumn()<=8);
    }

    static HashSet<ChessMove> makeStaticMoves(ChessPosition currentPosition, int[][] relMove, ChessBoard board) {
        HashSet<ChessMove> moves=new HashSet<>();
        int currX = currentPosition.getColumn();
        int currY = currentPosition.getRow();
        ChessGame.TeamColor pieceColor = board.getPieceTeam(currentPosition);

        for (int[] relMoves : relMove) {
            ChessPosition possPos= new ChessPosition(currY + relMoves[1], currX + relMoves[0]);
            if (!MovesCalc.isValidSquare(possPos)) {
                continue;
            }
            if (board.getPiece(possPos) == null || board.getPieceTeam(possPos) != pieceColor) {
                moves.add(new ChessMove(currentPosition, possPos, null));
            }
        }
        return moves;
    }

    static HashSet<ChessMove> makeDirection(ChessBoard board,
                                            ChessPosition currentPosition,
                                            int[][] moveDirections,
                                            int currY,
                                            int currX,
                                            ChessGame.TeamColor pieceColor) {
        HashSet<ChessMove> moves= new HashSet<>();
        for (int[] direction : moveDirections) {
            boolean obstructed = false;
            int i=1;
            while (!obstructed) {
                ChessPosition possiblePosition = new ChessPosition(currY + direction[1]*i, currX + direction[0]*i);
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
