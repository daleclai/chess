package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KingCalc {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int[][] relMoves = {
                {1, 1},
                {0, 1},
                {1, 0},
                {-1, 1},
                {-1, 0},
                {0, -1},
                {1, -1},
                {-1, -1}
        };
        return MovesCalc.makeStaticMoves(currentPosition, relMoves, board);
    }

}
