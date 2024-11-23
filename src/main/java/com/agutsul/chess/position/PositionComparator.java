package com.agutsul.chess.position;

import java.io.Serializable;
import java.util.Comparator;

public final class PositionComparator
        implements Comparator<Position>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Position position1, Position position2) {
        var compared = Integer.compare(position1.x(), position2.x());
        if (compared != 0) {
            return compared;
        }

        return Integer.compare(position1.y(), position2.y());
    }
}