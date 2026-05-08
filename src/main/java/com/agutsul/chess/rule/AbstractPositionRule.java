package com.agutsul.chess.rule;

import java.util.Collection;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.position.Position;

public abstract class AbstractPositionRule<SOURCE extends Position,
                                           RESULT extends Positionable & Activity<TYPE,?>,
                                           TYPE   extends Enum<TYPE> & Activity.Type>
        implements Rule<SOURCE,Collection<RESULT>> {

    protected final CompositeRule<SOURCE,RESULT,TYPE> compositeRule;

    protected AbstractPositionRule(CompositeRule<SOURCE,RESULT,TYPE> rule) {
        this.compositeRule = rule;
    }

    @Override
    public Collection<RESULT> evaluate(SOURCE source) {
        return compositeRule.evaluate(source);
    }
}