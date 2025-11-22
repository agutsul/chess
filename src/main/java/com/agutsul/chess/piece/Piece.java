package com.agutsul.chess.piece;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.Placeable;
import com.agutsul.chess.Positionable;
import com.agutsul.chess.Rankable;
import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

public interface Piece<COLOR extends Color>
        extends Positionable, Placeable, Valuable<Integer> {

    enum Type implements Rankable {
        PAWN("",    1),
        KNIGHT("N", 3),
        BISHOP("B", 3),
        ROOK("R",   5),
        QUEEN("Q",  9),
        KING("K", 400);

        private static final Map<String,Type> TYPES =
                Stream.of(values()).collect(toMap(Type::code, identity()));

        private String code;
        private int rank;

        Type(String code, int rank) {
            this.code = code;
            this.rank = rank;
        }

        public String code() {
            return code;
        }

        @Override
        public int rank() {
            return rank;
        }

        @Override
        public String toString() {
            return code();
        }

        public static Type codeOf(String code) {
            return TYPES.get(code);
        }
    }

    Type getType();
    COLOR getColor();
    String getUnicode();
    int getDirection();

    PieceState<Piece<COLOR>> getState();

    List<Position> getPositions();

    Collection<Action<?>> getActions();
    Collection<Action<?>> getActions(Action.Type actionType);

    Collection<Impact<?>> getImpacts();
    Collection<Impact<?>> getImpacts(Impact.Type impactType);

    boolean isActive();

    // utilities

    static boolean isPawn(Piece<?> piece) {
        return isPawn(piece.getType());
    }

    static boolean isPawn(Piece.Type pieceType) {
        return Piece.Type.PAWN.equals(pieceType);
    }

    static boolean isKnight(Piece<?> piece) {
        return isKnight(piece.getType());
    }

    static boolean isKnight(Piece.Type pieceType) {
        return Piece.Type.KNIGHT.equals(pieceType);
    }

    static boolean isBishop(Piece<?> piece) {
        return isBishop(piece.getType());
    }

    static boolean isBishop(Piece.Type pieceType) {
        return Piece.Type.BISHOP.equals(pieceType);
    }

    static boolean isRook(Piece<?> piece) {
        return isRook(piece.getType());
    }

    static boolean isRook(Piece.Type pieceType) {
        return Piece.Type.ROOK.equals(pieceType);
    }

    static boolean isQueen(Piece<?> piece) {
        return isQueen(piece.getType());
    }

    static boolean isQueen(Piece.Type pieceType) {
        return Piece.Type.QUEEN.equals(pieceType);
    }

    static boolean isKing(Piece<?> piece) {
        return isKing(piece.getType());
    }

    static boolean isKing(Piece.Type pieceType) {
        return Piece.Type.KING.equals(pieceType);
    }

    static boolean isLinear(Piece<?> piece) {
        return isLinear(piece.getType());
    }

    static boolean isLinear(Piece.Type pieceType) {
        return isBishop(pieceType) || isRook(pieceType) || isQueen(pieceType);
    }
}