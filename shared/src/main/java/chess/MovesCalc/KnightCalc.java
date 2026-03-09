package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightCalc {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int[][] relMoves = {
                {2,1},
                {-2,1},
                {2,-1},
                {-2,-1},
                {1,2},
                {-1,2},
                {1,-2},
                {-1,-2}
        };
        return MovesCalc.makeStaticMoves(currentPosition, relMoves, board);
    }

}
