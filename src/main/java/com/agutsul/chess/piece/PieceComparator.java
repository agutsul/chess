package com.agutsul.chess.piece;

import java.io.Serializable;
import java.util.Comparator;

public final class PieceComparator
        implements Comparator<Piece<?>>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Piece<?> piece1, Piece<?> piece2) {
        // sort most valuable pieces first
        return Integer.compare(
                piece2.getType().rank(),
                piece1.getType().rank()
        );
    }
}