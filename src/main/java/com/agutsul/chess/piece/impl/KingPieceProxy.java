package com.agutsul.chess.piece.impl;

import java.util.Collection;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.position.Position;

/**
 * Mainly used to prevent any usage of unsupported methods
 * received by extending AbstractPiece class
 */
final class KingPieceProxy
        extends AbstractPieceProxy<KingPiece<?>>
        implements KingPiece<Color> {

    KingPieceProxy(KingPiece<?> origin) {
        super(origin);
    }

    @Override
    public Collection<Action<?>> getActions() {
        return getState().calculateActions(this);
    }

    @Override
    public Collection<Action<?>> getActions(Action.Type actionType) {
        return getState().calculateActions(this, actionType);
    }

    @Override
    public Collection<Impact<?>> getImpacts() {
        return getState().calculateImpacts(this);
    }

    @Override
    public Collection<Impact<?>> getImpacts(Impact.Type impactType) {
        return getState().calculateImpacts(this, impactType);
    }

    @Override
    public void castling(Position position) {
        this.origin.castling(position);
    }

    @Override
    public void uncastling(Position position) {
        this.origin.uncastling(position);
    }

    @Override
    public boolean isChecked() {
        return this.origin.isChecked();
    }

    @Override
    public boolean isCheckMated() {
        return this.origin.isCheckMated();
    }

    @Override
    public void setChecked(boolean checked) {
        ((KingPiece<?>) this.origin).setChecked(checked);
    }

    @Override
    public void setCheckMated(boolean checkMated) {
        ((KingPiece<?>) this.origin).setCheckMated(checkMated);
    }
}