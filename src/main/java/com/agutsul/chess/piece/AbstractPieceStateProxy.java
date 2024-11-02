package com.agutsul.chess.piece;

import java.util.Collection;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceStateProxy<PIECE extends Piece<Color> & Movable & Capturable>
        extends AbstractPieceState<PIECE> {

    private final AbstractPieceState<PIECE> origin;

    AbstractPieceStateProxy(AbstractPieceState<PIECE> origin) {
        super(origin.getType());
        this.origin = origin;
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece) {
        return this.origin.calculateActions(piece);
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece) {
        return this.origin.calculateImpacts(piece);
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