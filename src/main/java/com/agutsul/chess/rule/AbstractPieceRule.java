package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.piece.Piece;

public abstract class AbstractPieceRule<RESULT extends Positionable & Activity<?>,
                                        TYPE extends Enum<TYPE> & Activity.Type>
        implements Rule<Piece<?>, Collection<RESULT>> {

    protected final CompositePieceRule<RESULT,TYPE> compositeRule;

    protected AbstractPieceRule(CompositePieceRule<RESULT,TYPE> rule) {
        this.compositeRule = rule;
    }

    @Override
    public Collection<RESULT> evaluate(Piece<?> piece) {
        return compositeRule.evaluate(piece);
    }

    @SuppressWarnings("unchecked")
    public Collection<RESULT> evaluate(Piece<?> piece, TYPE type, TYPE... additionalTypes) {
        return compositeRule.evaluate(piece, type, additionalTypes);
    }
}