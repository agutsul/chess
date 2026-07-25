package com.agutsul.chess.activity.impact;

import static java.util.Objects.nonNull;

import java.util.Optional;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractPieceAttackImpact<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                ATTACKER extends Piece<COLOR1> & Capturable,
                                                ATTACKED extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,ATTACKED>
        implements Impact<ATTACKER> {

    private final Calculatable calculated;
    private final boolean hidden;
    private final ImpactValueProvider valueProvider;

    protected AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED attacked,
                                        Calculatable calculated, boolean hidden) {

        super(impactType, attacker, attacked);

        this.calculated = calculated;
        this.hidden = hidden;
        this.valueProvider = new ImpactValueProvider(() -> calculateValue());
    }

    @Override
    public String toString() {
        return String.format("%s:%sx%s", getType(), getSource(), getTarget());
    }

    @Override
    public Integer getValue() {
        return this.valueProvider.get();
    }

    public final boolean isHidden() {
        return this.hidden;
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(nonNull(this.calculated) && this.calculated instanceof Line
                ? (Line) this.calculated
                : null
        );
    }

    @Override
    public Position getPosition() {
        return nonNull(this.calculated) && this.calculated instanceof Position
                ? (Position) this.calculated
                : getTarget().getPosition();
    }

    Integer calculateValue() {
        return Math.negateExact(getTarget().getValue());
    }
}