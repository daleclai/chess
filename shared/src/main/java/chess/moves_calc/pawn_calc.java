package chess.moves_calc;

import chess.*;

import java.util.HashSet;

public class pawn_calc {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        HashSet<ChessMove> moves = new HashSet<>(16);
        int current_x = currentPosition.getColumn();
        int current_y = currentPosition.getRow();
        ChessPiece.PieceType[] promoPieces = new ChessPiece.PieceType[]{null};
        ChessGame.TeamColor pieceColor = board.getPieceTeam(currentPosition);
        int moveIncrement = pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean promote = (pieceColor == ChessGame.TeamColor.WHITE && current_y == 7) || (pieceColor == ChessGame.TeamColor.BLACK && current_y == 2);
        if (promote) {
            promoPieces = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};
        }

        for (ChessPiece.PieceType promoPiece : promoPieces) {
            ChessPosition forwardPos = new ChessPosition(current_y + moveIncrement, current_x);
            if (MovesCalc.isValidSquare(forwardPos) && board.getPiece(forwardPos) == null) {
                moves.add(new ChessMove(currentPosition, forwardPos, promoPiece));
            }
            //Left atk//
            ChessPosition leftAtk = new ChessPosition(current_y + moveIncrement, current_x-1);
            if (MovesCalc.isValidSquare(leftAtk) && board.getPiece(leftAtk) != null && board.getPieceTeam(leftAtk) != pieceColor) {
                moves.add(new ChessMove(currentPosition, leftAtk, promoPiece));
            }
            //right atk//
            ChessPosition rightAtk = new ChessPosition(current_y + moveIncrement, current_x+1);
            if (MovesCalc.isValidSquare(rightAtk) && board.getPiece(rightAtk) != null && board.getPieceTeam(rightAtk) != pieceColor) {
                moves.add(new ChessMove(currentPosition, rightAtk, promoPiece));
            }
            // double forward //
            ChessPosition doubleForward = new ChessPosition(current_y + moveIncrement*2, current_x);
            if (MovesCalc.isValidSquare(doubleForward) &&
                ((pieceColor == ChessGame.TeamColor.WHITE && current_y == 2) ||
                (pieceColor == ChessGame.TeamColor.BLACK && current_y == 7)) &&
                board.getPiece(doubleForward) == null && board.getPieceTeam(doubleForward) == pieceColor) {
                    moves.add(new ChessMove(currentPosition, doubleForward, null));
            }

        }
        return moves;
    }
}
