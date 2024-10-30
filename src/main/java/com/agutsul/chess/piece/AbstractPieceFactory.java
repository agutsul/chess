package com.agutsul.chess.piece;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

abstract class AbstractPieceFactory<COLOR extends Color>
        implements PieceFactory {

    private static final Logger LOGGER = getLogger(AbstractPieceFactory.class);

    private static final PositionFactory POSITION_FACTORY = PositionFactory.INSTANCE;

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

    final Board board;
    final COLOR color;
    final Direction direction;
    final Promotion promotion;
    final BigMove bigMove;

    AbstractPieceFactory(Board board, COLOR color,
            Direction direction, Promotion promotion, BigMove bigMove) {

        this.board = board;
        this.color = color;
        this.direction = direction;
        this.promotion = promotion;
        this.bigMove = bigMove;
    }

    @Override
    public final KingPiece<Color> createKing(String code) {
        return createKing(POSITION_FACTORY.createPosition(code));
    }

    @Override
    public final QueenPiece<Color> createQueen(String code) {
        return createQueen(POSITION_FACTORY.createPosition(code));
    }

    @Override
    public final RookPiece<Color> createRook(String code) {
        return createRook(POSITION_FACTORY.createPosition(code));
    }

    @Override
    public final BishopPiece<Color> createBishop(String code) {
        return createBishop(POSITION_FACTORY.createPosition(code));
    }

    @Override
    public final KnightPiece<Color> createKnight(String code) {
        return createKnight(POSITION_FACTORY.createPosition(code));
    }

    @Override
    public final PawnPiece<Color> createPawn(String code) {
        return createPawn(POSITION_FACTORY.createPosition(code));
    }

    KingPiece<COLOR> createKing(Position position, String unicode) {
        LOGGER.debug("Create '{}' king at '{}'", color, position);
        return new KingPieceImpl<>(board, color, unicode, position, direction.code());
    }

    QueenPiece<COLOR> createQueen(Position position, String unicode) {
        LOGGER.debug("Create '{}' queen at '{}'", color, position);
        return new QueenPieceImpl<>(board, color, unicode, position, direction.code());
    }

    RookPiece<COLOR> createRook(Position position, String unicode) {
        LOGGER.debug("Create '{}' rook at '{}'", color, position);
        return new RookPieceImpl<>(board, color, unicode, position, direction.code());
    }

    BishopPiece<COLOR> createBishop(Position position, String unicode) {
        LOGGER.debug("Create '{}' bishop at '{}'", color, position);
        return new BishopPieceImpl<>(board, color, unicode, position, direction.code());
    }

    KnightPiece<COLOR> createKnight(Position position, String unicode) {
        LOGGER.debug("Create '{}' knight at '{}'", color, position);
        return new KnightPieceImpl<>(board, color, unicode, position, direction.code());
    }

    PawnPiece<COLOR> createPawn(Position position, String unicode) {
        LOGGER.debug("Create '{}' pawn at '{}'", color, position);
        return new PawnPieceImpl<>(board, color, unicode, position, direction.code(),
                promotion.line(), bigMove.line());
    }
}