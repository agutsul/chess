package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.activity.impact.PositionHoleImpact;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

public interface HoleImpactRule<POSITION extends Position,
                                IMPACT extends PositionHoleImpact<POSITION>>
        extends Rule<POSITION,Collection<IMPACT>> {

}