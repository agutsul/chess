package com.agutsul.chess.piece.impl;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Demotable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.Settable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceProxy;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.factory.PieceFactory;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceFactory<COLOR extends Color>
        implements PieceFactory {

    enum Directions implements Direction {
        UP(1),
        DOWN(-1);

        private int code;

        Directions(int code) {
            this.code = code;
        }

        @Override
        public int code() {
            return code;
        }

        @Override
        public String toString() {
            return name();
        }
    }

    enum Promotions implements Promotion {
        WHITE(Position.MAX - 1),
        BLACK(Position.MIN);

        private int line;

        Promotions(int line) {
            this.line = line;
        }

        @Override
        public int line() {
            return line;
        }

        @Override
        public String toString() {
            return name();
        }
    }

    enum BigMoves implements BigMove {
        WHITE(Position.MIN + 1),
        BLACK(Position.MAX - 2);

        private int line;

        BigMoves(int line) {
            this.line = line;
        }

        @Override
        public int line() {
            return line;
        }

        @Override
        public String toString() {
            return name();
        }
    }

    final Logger logger;
    final Board board;
    final COLOR color;
    final Direction direction;
    final Promotion promotion;
    final BigMove bigMove;

    AbstractPieceFactory(Logger logger, Board board, COLOR color,
                         Direction direction, Promotion promotion, BigMove bigMove) {

        this.logger = logger;
        this.board = board;
        this.color = color;
        this.direction = direction;
        this.promotion = promotion;
        this.bigMove = bigMove;
    }

    @Override
    public final KingPiece<Color> createKing(String code) {
        return createKing(positionOf(code));
    }

    @Override
    public final QueenPiece<Color> createQueen(String code) {
        return createQueen(positionOf(code));
    }

    @Override
    public final RookPiece<Color> createRook(String code) {
        return createRook(positionOf(code));
    }

    @Override
    public final BishopPiece<Color> createBishop(String code) {
        return createBishop(positionOf(code));
    }

    @Override
    public final KnightPiece<Color> createKnight(String code) {
        return createKnight(positionOf(code));
    }

    @Override
    public final PawnPiece<Color> createPawn(String code) {
        return createPawn(positionOf(code));
    }

    KingPiece<COLOR> createKing(Position position, String unicode) {
        logger.debug("Create '{}' king at '{}'", color, position);
        return new KingPieceImpl<>(board, color, unicode, position, direction.code());
    }

    QueenPiece<COLOR> createQueen(Position position, String unicode) {
        logger.debug("Create '{}' queen at '{}'", color, position);
        return new QueenPieceImpl<>(board, color, unicode, position, direction.code());
    }

    RookPiece<COLOR> createRook(Position position, String unicode) {
        logger.debug("Create '{}' rook at '{}'", color, position);
        return new RookPieceImpl<>(board, color, unicode, position, direction.code());
    }

    BishopPiece<COLOR> createBishop(Position position, String unicode) {
        logger.debug("Create '{}' bishop at '{}'", color, position);
        return new BishopPieceImpl<>(board, color, unicode, position, direction.code());
    }

    KnightPiece<COLOR> createKnight(Position position, String unicode) {
        logger.debug("Create '{}' knight at '{}'", color, position);
        return new KnightPieceImpl<>(board, color, unicode, position, direction.code());
    }

    PawnPiece<COLOR> createPawn(Position position, String unicode) {
        logger.debug("Create '{}' pawn at '{}'", color, position);
        return new PawnPieceImpl<>(board, color, unicode, position,
                                   direction.code(), promotion.line(), bigMove.line());
    }

    <PIECE extends Piece<COLOR> & Demotable,PROXY extends PieceProxy<PIECE> & Demotable>
        PROXY demotableProxy(PIECE piece) {

        return DemotablePieceProxyFactory.demotableProxy(piece);
    }

    <PIECE extends Piece<COLOR> & Pinnable,PROXY extends PieceProxy<PIECE> & Pinnable>
        PROXY pinnableProxy(PIECE piece) {

        return PinnablePieceProxyFactory.pinnableProxy(board, piece);
    }

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
        static <PIECE extends Piece<?> & Demotable,PROXY extends PieceProxy<PIECE> & Demotable>
                PROXY demotableProxy(PIECE piece) {

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
        static <PIECE extends Piece<?> & Pinnable,PROXY extends PieceProxy<PIECE> & Pinnable>
                PROXY pinnableProxy(Board board, PIECE piece) {

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
                this.origin.castling(position);
            }

            @Override
            public void uncastling(Position position) {
                logger.info("Cancel castling for piece '{}'", this);
                this.origin.uncastling(position);
            }

            @Override
            public void set(Settable.Type type, Object value) {
                this.origin.set(type, value);
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
                this.origin.promote(position, pieceType);
            }

            @Override
            public void enpassant(PawnPiece<?> targetPiece, Position targetPosition) {
                logger.info("En-passant piece '{}' by '{}' and move to '{}'",
                        targetPiece, this, targetPosition
                );

                this.origin.enpassant(targetPiece, targetPosition);
            }

            @Override
            public void unenpassant(PawnPiece<?> targetPiece) {
                logger.info("Cancel en-passant actions for piece '{}'", this);
                this.origin.unenpassant(targetPiece);
            }

            @Override
            public boolean isBlocked() {
                logger.info("Check if piece '{}' is blocked", this);
                return this.origin.isBlocked();
            }

            @Override
            public void set(Settable.Type type, Object value) {
                logger.info("Set piece '{}' en-passant position '{}'", this, value);
                this.origin.set(type, value);
            }
        }
    }
}