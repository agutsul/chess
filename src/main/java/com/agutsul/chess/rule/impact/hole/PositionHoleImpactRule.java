package com.agutsul.chess.rule.impact.hole;

import java.util.Collection;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PositionHoleImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractPositionRule;
import com.agutsul.chess.rule.AbstractRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.impact.HoleImpactRule;

public final class PositionHoleImpactRule<POSITION extends Position,
                                          IMPACT   extends PositionHoleImpact<POSITION>>
        extends AbstractRule<POSITION,IMPACT,Impact.Type>
        implements HoleImpactRule<POSITION,IMPACT> {

    private HoleImpactRule<POSITION,IMPACT> rule;

    public PositionHoleImpactRule(Board board, Color color) {
        super(board, Impact.Type.HOLE);
        this.rule = new CompositePositionHoleImpactRule<>(board, color);
    }

    @Override
    public Collection<IMPACT> evaluate(POSITION position) {
        return this.rule.evaluate(position);
    }

    private static final class CompositePositionHoleImpactRule<POSITION extends Position,
                                                               IMPACT   extends PositionHoleImpact<POSITION>>
            extends AbstractPositionRule<POSITION,IMPACT,Impact.Type>
            implements HoleImpactRule<POSITION,IMPACT> {

        @SuppressWarnings("unchecked")
        public CompositePositionHoleImpactRule(Board board, Color color) {
            super(new CompositeRule<>(
                    (HoleImpactRule<POSITION,IMPACT>) new PositionAbsoluteHoleImpactRule(board, color),
                    (HoleImpactRule<POSITION,IMPACT>) new PositionRelativeHoleImpactRule(board, color)
            ));
        }
    }
}