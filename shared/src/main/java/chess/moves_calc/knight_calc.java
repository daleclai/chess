package chess.moves_calc;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class knight_calc {
    public static HashSet<ChessMove> getMove(ChessBoard board, ChessPosition currentPosition) {
        int[][] rel_Moves = {
                {2,1},
                {-2,1},
                {2,-1},
                {-2,-1},
                {1,2},
                {-1,2},
                {1,-2},
                {-1,-2}
        };
        return MovesCalc.makeStaticMoves(currentPosition, rel_Moves, board);
    }

}
