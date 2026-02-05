package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;
    private boolean game_over;

    public ChessGame() {
        board = new ChessBoard();
        setTeamTurn(TeamColor.WHITE);
        game_over = false;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        @Override
        public String toString() {
            return this == WHITE ? "white" : "black";
        }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece current = board.getPiece(startPosition);
        if (current==null) return null;
        Collection<ChessMove> possible = current.pieceMoves(board, startPosition);
        Collection<ChessMove> valid = new HashSet<>();
        for (ChessMove move : possible) {
            ChessPiece captured = board.getPiece(move.getEndPosition());
            if (move.getPromotionPiece() != null) {
                current = new ChessPiece(current.getTeamColor(), move.getPromotionPiece());
            }
            board.addPiece(startPosition, null);
            board.addPiece(move.getEndPosition(), current);

            if (!isInCheck(current.getTeamColor())) {
                valid.add(move);
            }

            board.addPiece(move.getEndPosition(), captured);
            board.addPiece(startPosition, current);
        }
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        ChessPiece moved = piece;
        if (move.getPromotionPiece() != null) {
            moved = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Not your turn");
        }

        ChessPiece captured = board.getPiece(move.getEndPosition());

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), moved);

        if (isInCheck(piece.getTeamColor())) {
            board.addPiece(move.getStartPosition(), piece);
            board.addPiece(move.getEndPosition(), moved);
            throw new InvalidMoveException("King is in check");
        }

        setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);

        if (isInCheckmate(getTeamTurn()) || isInStalemate(getTeamTurn())) {
            game_over = true;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
