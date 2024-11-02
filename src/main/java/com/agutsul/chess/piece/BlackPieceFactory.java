package com.agutsul.chess.piece;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.PawnPiece.PawnPieceProxy;
import com.agutsul.chess.position.Position;

public final class BlackPieceFactory
        extends AbstractPieceFactory<Color> {

    private static final String KING_UNICODE    = "\u265A";
    private static final String QUEEN_UNICODE   = "\u265B";
    private static final String ROOK_UNICODE    = "\u265C";
    private static final String BISHOP_UNICODE  = "\u265D";
    private static final String KNIGHT_UNICODE  = "\u265E";
    private static final String PAWN_UNICODE    = "\u265F";

    public BlackPieceFactory(Board board) {
        super(board, Colors.BLACK, Directions.DOWN, Promotions.BLACK, BigMoves.BLACK);
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