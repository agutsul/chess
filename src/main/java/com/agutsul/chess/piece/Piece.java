package com.agutsul.chess.piece;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;

public interface Piece<COLOR extends Color>
        extends Positionable {

    enum Type {
        PAWN("",    1),
        KNIGHT("N", 3),
        BISHOP("B", 3),
        ROOK("R",   5),
        QUEEN("Q",  9),
        KING("K", 400);

        private static final Map<String,Type> TYPES =
                Stream.of(values()).collect(toMap(Type::code, identity()));

        private String code;
        private int value;

        Type(String code, int value) {
            this.code = code;
            this.value = value;
        }

        public String code() {
            return code;
        }

        public int value() {
            return value;
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
    int getValue();

    PieceState<Piece<COLOR>> getState();

    List<Position> getPositions();

    Collection<Action<?>> getActions();
    Collection<Action<?>> getActions(Action.Type actionType);

    Collection<Impact<?>> getImpacts();
    Collection<Impact<?>> getImpacts(Impact.Type impactType);

    boolean isActive();
}