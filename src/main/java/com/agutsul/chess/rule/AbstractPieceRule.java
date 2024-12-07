package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Positionable;

public abstract class AbstractPieceRule<P extends Positionable>
        implements Rule<Piece<?>, Collection<P>> {

    protected final Rule<Piece<?>, Collection<P>> rule;

    protected AbstractPieceRule(Rule<Piece<?>, Collection<P>> rule) {
        this.rule = rule;
    }

    @Override
    public Collection<P> evaluate(Piece<?> piece) {
        return rule.evaluate(piece);
    }
}