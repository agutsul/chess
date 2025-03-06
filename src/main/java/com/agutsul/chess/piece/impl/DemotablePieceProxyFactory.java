package com.agutsul.chess.piece.impl;

import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Demotable;
import com.agutsul.chess.Settable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceProxy;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.position.Position;

enum DemotablePieceProxyFactory {
    BISHOP_MODE(Piece.Type.BISHOP, piece -> new DemotableBishopPieceProxy<>((BishopPiece<?>) piece)),
    KNIGHT_MODE(Piece.Type.KNIGHT, piece -> new DemotableKnightPieceProxy<>((KnightPiece<?>) piece)),
    QUEEN_MODE(Piece.Type.QUEEN,   piece -> new DemotableQueenPieceProxy<>((QueenPiece<?>) piece)),
    ROOK_MODE(Piece.Type.ROOK,     piece -> new DemotableRookPieceProxy<>((RookPiece<?>) piece));

    private static final Map<Piece.Type,Function<Piece<?>,AbstractDemotablePieceProxy<?>>> MODES =
            Stream.of(values()).collect(toMap(DemotablePieceProxyFactory::type, DemotablePieceProxyFactory::function));

    private Piece.Type pieceType;
    private Function<Piece<?>,AbstractDemotablePieceProxy<?>> function;

    DemotablePieceProxyFactory(Piece.Type pieceType,
                               Function<Piece<?>,AbstractDemotablePieceProxy<?>> function) {

        this.pieceType = pieceType;
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    static <PIECE extends Piece<?> & Demotable,PROXY extends PieceProxy<PIECE> & Demotable> PROXY demotableProxy(PIECE piece) {

        if (piece == null) {
            return null;
        }

        var function = MODES.get(piece.getType());
        if (function == null) {
            return null;
        }

        return (PROXY) function.apply(piece);
    }

    private Function<Piece<?>,AbstractDemotablePieceProxy<?>> function() {
        return function;
    }

    private Piece.Type type() {
        return pieceType;
    }

    // actual demotable proxy implementations

    private static final class DemotableBishopPieceProxy<PIECE extends BishopPiece<?>>
            extends AbstractDemotablePieceProxy<PIECE>
            implements BishopPiece<Color> {

        private static final Logger LOGGER = getLogger(DemotableBishopPieceProxy.class);

        DemotableBishopPieceProxy(PIECE origin) {
            super(LOGGER, origin);
        }

        @Override
        public boolean isPinned() {
            return this.origin.isPinned();
        }
    }

    private static final class DemotableKnightPieceProxy<PIECE extends KnightPiece<?>>
            extends AbstractDemotablePieceProxy<PIECE>
            implements KnightPiece<Color> {

        private static final Logger LOGGER = getLogger(DemotableKnightPieceProxy.class);

        DemotableKnightPieceProxy(PIECE origin) {
            super(LOGGER, origin);
        }

        @Override
        public boolean isPinned() {
            return this.origin.isPinned();
        }
    }

    private static final class DemotableQueenPieceProxy<PIECE extends QueenPiece<?>>
            extends AbstractDemotablePieceProxy<PIECE>
            implements QueenPiece<Color> {

        private static final Logger LOGGER = getLogger(DemotableQueenPieceProxy.class);

        DemotableQueenPieceProxy(PIECE origin) {
            super(LOGGER, origin);
        }

        @Override
        public boolean isPinned() {
            return this.origin.isPinned();
        }
    }

    private static final class DemotableRookPieceProxy<PIECE extends RookPiece<?>>
            extends AbstractDemotablePieceProxy<PIECE>
            implements RookPiece<Color> {

        private static final Logger LOGGER = getLogger(DemotableRookPieceProxy.class);

        DemotableRookPieceProxy(PIECE origin) {
            super(LOGGER, origin);
        }

        @Override
        public void castling(Position position) {
            this.origin.castling(position);
        }

        @Override
        public void uncastling(Position position) {
            this.origin.uncastling(position);
        }

        @Override
        public void set(Settable.Type type, Object value) {
            this.origin.set(type, value);
        }

        @Override
        public boolean isPinned() {
            return this.origin.isPinned();
        }
    }
}