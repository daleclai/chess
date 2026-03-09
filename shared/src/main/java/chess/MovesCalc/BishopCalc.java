package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class BishopCalc {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int currX = currentPosition.getColumn();
        int currY = currentPosition.getRow();
        int[][] directions = {
                {1,1},
                {-1,1},
                {1,-1},
                {-1,-1}
        };
        ChessGame.TeamColor pieceColor = board.getPieceTeam(currentPosition);
        return MovesCalc.makeDirection(board, currentPosition, directions, currY, currX, pieceColor);
    }

}
