package com.agutsul.chess.piece;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.state.PieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.Positionable;

public interface Piece<COLOR extends Color>
        extends Positionable {

    enum Type {
        PAWN(""),
        KNIGHT("N"),
        BISHOP("B"),
        ROOK("R"),
        QUEEN("Q"),
        KING("K");

        private String code;

        private Type(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

    Type getType();
    COLOR getColor();
    String getUnicode();

    PieceState<Piece<Color>> getState();

    List<Position> getPositions();

    Collection<Action<?>> getActions();
    Collection<Impact<?>> getImpacts();

    boolean isActive();
    boolean isMoved();
}