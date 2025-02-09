package com.agutsul.chess.piece;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceStateProxy<PIECE extends Piece<?> & Movable & Capturable>
        extends AbstractPieceState<PIECE> {

    protected final AbstractPieceState<PIECE> origin;

    AbstractPieceStateProxy(AbstractPieceState<PIECE> origin) {
        super(origin.getType());
        this.origin = origin;
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece) {
        return this.origin.calculateActions(piece);
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType) {
        return this.origin.calculateActions(piece, actionType);
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece) {
        return this.origin.calculateImpacts(piece);
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece, Impact.Type impactType) {
        return this.origin.calculateImpacts(piece, impactType);
    }

    @Override
    public void move(PIECE piece, Position position) {
        this.origin.move(piece, position);
    }

    @Override
    public void capture(PIECE piece, Piece<?> targetPiece) {
        this.origin.capture(piece, targetPiece);
    }
}