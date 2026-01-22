package chess;
import chess.moves_calc.*;

import java.util.Collection;
import java.util.Objects;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;

    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }


    /**
     * @return which type of chess piece this piece is
     */

    public PieceType getPieceType() {
        return type;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return switch (type) {
            case BISHOP -> bishop_calc.getMoves(board, position);
            case KING -> king_calc.getMoves(board, position);
            case KNIGHT -> knight_calc.getMoves(board, position);
            case PAWN -> pawn_calc.getMoves(board, position);
            case QUEEN -> queen_calc.getMoves(board, position);
            case ROOK -> rook_calc.getMoves(board, position);
        };
    }
    @Override
    public String toString() {
        return switch (type) {
            case BISHOP -> pieceColor == ChessGame.TeamColor.WHITE ? "B" : "b";
            case KING -> pieceColor == ChessGame.TeamColor.WHITE ? "K" : "k";
            case KNIGHT -> pieceColor == ChessGame.TeamColor.WHITE ? "N" : "n";
            case PAWN -> pieceColor == ChessGame.TeamColor.WHITE ? "P" : "p";
            case QUEEN -> pieceColor == ChessGame.TeamColor.WHITE ? "Q" : "q";
            case ROOK -> pieceColor == ChessGame.TeamColor.WHITE ? "R" : "r";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o==null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

}

