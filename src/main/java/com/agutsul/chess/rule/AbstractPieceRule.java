package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.piece.Piece;

public abstract class AbstractPieceRule<SOURCE extends Piece<?>,
                                        RESULT extends Positionable & Activity<TYPE,?>,
                                        TYPE   extends Enum<TYPE> & Activity.Type>
        implements Rule<SOURCE,Collection<RESULT>> {

    protected final CompositeRule<SOURCE,RESULT,TYPE> compositeRule;

    protected AbstractPieceRule(CompositeRule<SOURCE,RESULT,TYPE> rule) {
        this.compositeRule = rule;
    }

    @Override
    public Collection<RESULT> evaluate(SOURCE source) {
        return compositeRule.evaluate(source);
    }

    @SuppressWarnings("unchecked")
    public final Collection<RESULT> evaluate(SOURCE source, TYPE type, TYPE... additionalTypes) {
        return compositeRule.evaluate(source, type, additionalTypes);
    }
}