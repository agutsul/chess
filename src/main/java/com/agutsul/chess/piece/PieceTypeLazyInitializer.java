package com.agutsul.chess.piece;

import org.apache.commons.lang3.concurrent.LazyInitializer;

import com.agutsul.chess.activity.action.PiecePromoteAction;

public final class PieceTypeLazyInitializer
        extends LazyInitializer<Piece.Type> {

    private final PiecePromoteAction<?,?> action;

    public PieceTypeLazyInitializer(PiecePromoteAction<?,?> action) {
        this.action = action;
    }

    @Override
    protected Piece.Type initialize() {
        return action.getPieceType();
    }
}