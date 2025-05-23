package com.agutsul.chess.piece.impl;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;

public final class BlackPieceFactory
        extends AbstractPieceFactory<Color> {

    private static final Logger LOGGER = getLogger(BlackPieceFactory.class);

    private static final String KING_UNICODE    = "\u265A";
    private static final String QUEEN_UNICODE   = "\u265B";
    private static final String ROOK_UNICODE    = "\u265C";
    private static final String BISHOP_UNICODE  = "\u265D";
    private static final String KNIGHT_UNICODE  = "\u265E";
    private static final String PAWN_UNICODE    = "\u265F";

    public BlackPieceFactory(Board board) {
        super(LOGGER, board, Colors.BLACK, Directions.BLACK, Promotions.BLACK, BigMoves.BLACK);
    }

    @Override
    public KingPiece<Color> createKing(Position position) {
        return new KingPieceProxy<>(super.createKing(position, KING_UNICODE));
    }

    @Override
    public QueenPiece<Color> createQueen(Position position) {
        return demotableProxy(pinnableProxy(super.createQueen(position, QUEEN_UNICODE)));
    }

    @Override
    public RookPiece<Color> createRook(Position position) {
        return demotableProxy(pinnableProxy(super.createRook(position, ROOK_UNICODE)));
    }

    @Override
    public BishopPiece<Color> createBishop(Position position) {
        return demotableProxy(pinnableProxy(super.createBishop(position, BISHOP_UNICODE)));
    }

    @Override
    public KnightPiece<Color> createKnight(Position position) {
        return demotableProxy(pinnableProxy(super.createKnight(position, KNIGHT_UNICODE)));
    }

    @Override
    public PawnPiece<Color> createPawn(Position position) {
        var piece = new TransformablePieceImpl<>(board, this,
                super.createPawn(position, PAWN_UNICODE),
                promotion.line()
        );

        return new TransformablePieceAdapter<>(pinnableProxy(piece));
    }
}