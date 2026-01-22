package chess.moves_calc;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class king_calc {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int[][] rel_Moves = {
                {1, 1},
                {0, 1},
                {1, 0},
                {-1, 1},
                {-1, 0},
                {0, -1},
                {1, -1},
                {-1, -1}
        };
        return MovesCalc.makeStaticMoves(currentPosition, rel_Moves, board);
    }

}
