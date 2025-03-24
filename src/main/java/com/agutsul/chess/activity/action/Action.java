package com.agutsul.chess.activity.action;

import com.agutsul.chess.Executable;
import com.agutsul.chess.Positionable;
import com.agutsul.chess.Rankable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.piece.Piece;

public interface Action<SOURCE>
        extends Executable,
                Positionable,
                Comparable<Action<?>>,
                Activity<Action.Type,SOURCE> {

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
    Piece<?> getPiece();

    @Override
    default int compareTo(Action<?> action) {
        var rank1 = getType().rank();
        var rank2 = action.getType().rank();

        return Integer.compare(rank2, rank1);
    }
}