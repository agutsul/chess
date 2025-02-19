package com.agutsul.chess.piece;

import static com.agutsul.chess.piece.PinnablePieceProxyFactory.pinnableProxy;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.position.Position;

public final class WhitePieceFactory
        extends AbstractPieceFactory<Color> {

    private static final Logger LOGGER = getLogger(WhitePieceFactory.class);

    private static final String KING_UNICODE    = "\u2654";
    private static final String QUEEN_UNICODE   = "\u2655";
    private static final String ROOK_UNICODE    = "\u2656";
    private static final String BISHOP_UNICODE  = "\u2657";
    private static final String KNIGHT_UNICODE  = "\u2658";
    private static final String PAWN_UNICODE    = "\u2659";

    public WhitePieceFactory(Board board) {
        super(LOGGER, board, Colors.WHITE, Directions.UP, Promotions.WHITE, BigMoves.WHITE);
    }

    @Override
    public KingPiece<Color> createKing(Position position) {
        return new KingPieceProxy(super.createKing(position, KING_UNICODE));
    }

    @Override
    public QueenPiece<Color> createQueen(Position position) {
        return pinnableProxy(board, super.createQueen(position, QUEEN_UNICODE));
    }

    @Override
    public RookPiece<Color> createRook(Position position) {
        return pinnableProxy(board, super.createRook(position, ROOK_UNICODE));
    }

    @Override
    public BishopPiece<Color> createBishop(Position position) {
        return pinnableProxy(board, super.createBishop(position, BISHOP_UNICODE));
    }

    @Override
    public KnightPiece<Color> createKnight(Position position) {
        return pinnableProxy(board, super.createKnight(position, KNIGHT_UNICODE));
    }

    @Override
    public PawnPiece<Color> createPawn(Position position) {
        var piece = super.createPawn(position, PAWN_UNICODE);
        return pinnableProxy(board, new PromotablePieceProxy<>(board, piece, promotion.line(), this));
    }
}