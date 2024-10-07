package com.agutsul.chess.piece;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.state.AbstractPieceState;
import com.agutsul.chess.position.Position;

class DisposedPieceState<PIECE extends Piece<Color> & Movable & Capturable>
        extends AbstractPieceState<PIECE> {

    DisposedPieceState() {
        super(Type.INACTIVE);
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece) {
        return emptyList();
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece) {
        return emptyList();
    }

    @Override
    public void move(PIECE piece, Position position) {
        // do nothing because piece is disposed
    }

    @Override
    public void capture(PIECE piece, Piece<?> targetPiece) {
        // do nothing because piece is disposed
    }
}