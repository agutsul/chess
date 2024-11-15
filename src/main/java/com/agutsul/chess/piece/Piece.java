package com.agutsul.chess.piece;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.Positionable;

public interface Piece<COLOR extends Color>
        extends Positionable {

    enum Type {
        PAWN("",    1),
        KNIGHT("N", 3),
        BISHOP("B", 3),
        ROOK("R",   5),
        QUEEN("Q",  9),
        KING("K", 400);

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
    }

    Type getType();
    COLOR getColor();
    String getUnicode();
    int getValue();

    PieceState<COLOR,Piece<COLOR>> getState();

    List<Position> getPositions();

    Collection<Action<?>> getActions();
    Collection<Impact<?>> getImpacts();

    boolean isActive();
    boolean isMoved();
}