package chess.moves_calc;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class rook_calc {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int current_x = currentPosition.getColumn();
        int current_y = currentPosition.getRow();
        int[][] moveDirections = {
                {0,1},
                {1,0},
                {-1,0},
                {0,-1}
        };
        ChessGame.TeamColor pieceColor = board.getPieceTeam(currentPosition);
        return MovesCalc.makeDirection(board, currentPosition, moveDirections, current_y, current_x, pieceColor);

    }
}
