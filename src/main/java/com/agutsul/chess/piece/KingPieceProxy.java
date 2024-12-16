package com.agutsul.chess.piece;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

/**
 * Mainly used to prevent any usage of unsupported methods
 * received by extending AbstractPiece class
 */
final class KingPieceProxy extends PieceProxy
        implements KingPiece<Color> {

    KingPieceProxy(KingPiece<Color> origin) {
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
    public void move(Position position) {
        ((Movable) this.origin).move(position);
    }

    @Override
    public void unmove(Position position) {
        ((Movable) this.origin).unmove(position);
    }

    @Override
    public void capture(Piece<?> targetPiece) {
        ((Capturable) this.origin).capture(targetPiece);
    }

    @Override
    public void uncapture(Piece<?> targetPiece) {
        ((Capturable) this.origin).uncapture(targetPiece);
    }

    @Override
    public boolean isChecked() {
        return ((Checkable) this.origin).isChecked();
    }

    @Override
    public boolean isCheckMated() {
        return ((Checkable) this.origin).isCheckMated();
    }

    @Override
    public boolean isProtected() {
        return ((Protectable) this.origin).isProtected();
    }

    @Override
    public void castling(Position position) {
        ((Castlingable) this.origin).castling(position);
    }

    @Override
    public void uncastling(Position position) {
        ((Castlingable) this.origin).uncastling(position);
    }
}