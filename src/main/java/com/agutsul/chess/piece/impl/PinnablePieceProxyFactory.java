package com.agutsul.chess.piece.impl;

import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceProxy;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;

enum PinnablePieceProxyFactory {
    BISHOP_MODE(Piece.Type.BISHOP, (board,piece) -> new PinnableBishopPieceProxy<>(board, (BishopPiece<?>) piece)),
    KNIGHT_MODE(Piece.Type.KNIGHT, (board,piece) -> new PinnableKnightPieceProxy<>(board, (KnightPiece<?>) piece)),
    QUEEN_MODE(Piece.Type.QUEEN,   (board,piece) -> new PinnableQueenPieceProxy<>(board,  (QueenPiece<?>) piece)),
    ROOK_MODE(Piece.Type.ROOK,     (board,piece) -> new PinnableRookPieceProxy<>(board,   (RookPiece<?>) piece)),
    PAWN_MODE(Piece.Type.PAWN,     (board,piece) -> new PinnablePawnPieceProxy<>(board,   (PawnPiece<?>) piece));

    private static final Map<Piece.Type,BiFunction<Board,Piece<?>,AbstractPinnablePieceProxy<?>>> MODES =
            Stream.of(values()).collect(toMap(PinnablePieceProxyFactory::type, PinnablePieceProxyFactory::function));

    private Piece.Type pieceType;
    private BiFunction<Board,Piece<?>,AbstractPinnablePieceProxy<?>> function;

    PinnablePieceProxyFactory(Piece.Type pieceType,
                              BiFunction<Board,Piece<?>,AbstractPinnablePieceProxy<?>> function) {

        this.pieceType = pieceType;
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    static <PIECE extends Piece<?> & Pinnable,PROXY extends PieceProxy<PIECE> & Pinnable> PROXY pinnableProxy(Board board, PIECE piece) {

        if (board == null || piece == null) {
            return null;
        }

        var function = MODES.get(piece.getType());
        if (function == null) {
            return null;
        }

        return (PROXY) function.apply(board, piece);
    }

    private BiFunction<Board,Piece<?>,AbstractPinnablePieceProxy<?>> function() {
        return function;
    }

    private Piece.Type type() {
        return pieceType;
    }

    // actual pinnable proxy implementations

    private static final class PinnableBishopPieceProxy<PIECE extends BishopPiece<?>>
            extends AbstractPinnablePieceProxy<PIECE>
            implements BishopPiece<Color> {

        private static final Logger LOGGER = getLogger(PinnableBishopPieceProxy.class);

        PinnableBishopPieceProxy(Board board, PIECE origin) {
            super(LOGGER, board, origin);
        }
    }

    private static final class PinnableKnightPieceProxy<PIECE extends KnightPiece<?>>
            extends AbstractPinnablePieceProxy<PIECE>
            implements KnightPiece<Color> {

        private static final Logger LOGGER = getLogger(PinnableKnightPieceProxy.class);

        PinnableKnightPieceProxy(Board board, PIECE origin) {
            super(LOGGER, board, origin);
        }
    }

    private static final class PinnableQueenPieceProxy<PIECE extends QueenPiece<?>>
            extends AbstractPinnablePieceProxy<PIECE>
            implements QueenPiece<Color> {

        private static final Logger LOGGER = getLogger(PinnableQueenPieceProxy.class);

        PinnableQueenPieceProxy(Board board, PIECE origin) {
            super(LOGGER, board, origin);
        }
    }

    private static final class PinnableRookPieceProxy<PIECE extends RookPiece<?>>
            extends AbstractPinnablePieceProxy<PIECE>
            implements RookPiece<Color> {

        private static final Logger LOGGER = getLogger(PinnableRookPieceProxy.class);

        PinnableRookPieceProxy(Board board, PIECE origin) {
            super(LOGGER, board, origin);
        }

        @Override
        public void castling(Position position) {
            logger.info("Castling for piece '{}'", this);
            origin.castling(position);
        }

        @Override
        public void uncastling(Position position) {
            logger.info("Cancel castling for piece '{}'", this);
            origin.uncastling(position);
        }
    }

    private static final class PinnablePawnPieceProxy<PIECE extends PawnPiece<?>>
            extends AbstractPinnablePieceProxy<PIECE>
            implements PawnPiece<Color> {

        private static final Logger LOGGER = getLogger(PinnablePawnPieceProxy.class);

        PinnablePawnPieceProxy(Board board, PIECE origin) {
            super(LOGGER, board, origin);
        }

        @Override
        public void promote(Position position, Piece.Type pieceType) {
            logger.info("Promote piece '{}' to '{}'", this, pieceType.name());
            origin.promote(position, pieceType);
        }

        @Override
        public void enpassant(PawnPiece<?> targetPiece, Position targetPosition) {
            logger.info("En-passant piece '{}' by '{}' and move to '{}'",
                    targetPiece, this, targetPosition
            );

            origin.enpassant(targetPiece, targetPosition);
        }

        @Override
        public void unenpassant(PawnPiece<?> targetPiece) {
            logger.info("Cancel en-passant actions for piece '{}'", this);
            origin.unenpassant(targetPiece);
        }

        @Override
        public boolean isBlocked() {
            logger.info("Check if piece '{}' is blocked", this);
            return origin.isBlocked();
        }
    }
}