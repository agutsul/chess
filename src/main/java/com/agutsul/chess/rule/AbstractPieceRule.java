package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Positionable;

public abstract class AbstractPieceRule<P extends Positionable,TYPE extends Enum<TYPE>>
        implements Rule<Piece<?>, Collection<P>> {

    protected final CompositePieceRule<P,TYPE> rule;

    protected AbstractPieceRule(CompositePieceRule<P,TYPE> rule) {
        this.rule = rule;
    }

    @Override
    public Collection<P> evaluate(Piece<?> piece) {
        return rule.evaluate(piece);
    }

    public Collection<P> evaluate(Piece<?> piece, TYPE type) {
        return rule.evaluate(piece, type);
    }
}