package com.agutsul.chess.piece;

import com.agutsul.chess.Color;
import com.agutsul.chess.Colors;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.PawnPiece.PawnPieceProxy;
import com.agutsul.chess.position.Position;

public final class WhitePieceFactory extends AbstractPieceFactory<Color> {

    private static final String KING_UNICODE    = "\u2654";
    private static final String QUEEN_UNICODE   = "\u2655";
    private static final String ROOK_UNICODE    = "\u2656";
    private static final String BISHOP_UNICODE  = "\u2657";
    private static final String KNIGHT_UNICODE  = "\u2658";
    private static final String PAWN_UNICODE    = "\u2659";

    public WhitePieceFactory(Board board) {
        super(board, Colors.WHITE, Directions.UP, Promotions.WHITE, BigMoves.WHITE);
    }

    @Override
    public KingPiece<Color> createKing(Position position) {
        return super.createKing(position, KING_UNICODE);
    }

    @Override
    public QueenPiece<Color> createQueen(Position position) {
        return super.createQueen(position, QUEEN_UNICODE);
    }

    @Override
    public RookPiece<Color> createRook(Position position) {
        return super.createRook(position, ROOK_UNICODE);
    }

    @Override
    public BishopPiece<Color> createBishop(Position position) {
        return super.createBishop(position, BISHOP_UNICODE);
    }

    @Override
    public KnightPiece<Color> createKnight(Position position) {
        return super.createKnight(position, KNIGHT_UNICODE);
    }

    @Override
    public PawnPiece<Color> createPawn(Position position) {
        var pawn = super.createPawn(position, PAWN_UNICODE);
        return new PawnPieceProxy(board, pawn, promotion.line(), this);
    }
}