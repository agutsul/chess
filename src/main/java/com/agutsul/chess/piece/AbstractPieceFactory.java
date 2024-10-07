package com.agutsul.chess.piece;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.PositionFactory;

abstract class AbstractPieceFactory<COLOR extends Color>
        implements PieceFactory {

    private static final PositionFactory POSITION_FACTORY = PositionFactory.INSTANCE;

    enum Directions implements Direction {
        UP(1),
        DOWN(-1);

        private int code;

        private Directions(int code) {
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

        private Promotions(int line) {
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

        private BigMoves(int line) {
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
        return new KingPieceImpl<>(board, color, unicode, position);
    }

    QueenPiece<COLOR> createQueen(Position position, String unicode) {
        return new QueenPieceImpl<>(board, color, unicode, position);
    }

    RookPiece<COLOR> createRook(Position position, String unicode) {
        return new RookPieceImpl<>(board, color, unicode, position);
    }

    BishopPiece<COLOR> createBishop(Position position, String unicode) {
        return new BishopPieceImpl<>(board, color, unicode, position);
    }

    KnightPiece<COLOR> createKnight(Position position, String unicode) {
        return new KnightPieceImpl<>(board, color, unicode, position);
    }

    PawnPiece<COLOR> createPawn(Position position, String unicode) {
        return new PawnPieceImpl<>(board, color, unicode, position,
                direction.code(), promotion.line(), bigMove.line());
    }
}
