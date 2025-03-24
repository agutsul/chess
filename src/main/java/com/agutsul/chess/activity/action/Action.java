package com.agutsul.chess.activity.action;

import com.agutsul.chess.Executable;
import com.agutsul.chess.Positionable;
import com.agutsul.chess.Rankable;
import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public interface Action<SOURCE>
        extends Executable, Positionable, Valuable,
                Comparable<Action<?>>, Activity<Action.Type,SOURCE> {

    enum Type implements Activity.Type, Rankable {
        PROMOTE,
        EN_PASSANT,
        CAPTURE,
        CASTLING,
        MOVE;

        @Override
        public int rank() {
            return values().length - ordinal();
        }
    }

    String getCode();

    <COLOR extends Color,PIECE extends Piece<COLOR>> PIECE getPiece();

    @Override
    default int compareTo(Action<?> action) {
        var rank1 = getType().rank();
        var rank2 = action.getType().rank();

        return Integer.compare(rank2, rank1);
    }

    @Override
    default int getValue() {
        return getType().rank() * getPiece().getDirection();
    }
}