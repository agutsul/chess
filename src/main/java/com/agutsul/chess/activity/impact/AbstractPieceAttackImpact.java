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

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece,
                              Calculatable calculated, boolean hidden) {

        super(impactType, attacker, piece);

        this.calculated = calculated;
        this.hidden = hidden;
    }

    public final boolean isHidden() {
        return this.hidden;
    }

    @Override
    public final Integer getValue() {
        var value = Math.abs(getSource().getValue()) - Math.abs(getTarget().getValue());
        return Impact.super.getValue() * Math.abs(getTarget().getValue()) + Math.abs(value);
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(nonNull(this.calculated) && this.calculated instanceof Line
                ? (Line) this.calculated
                : null
        );
    }

    @Override
    public final Position getPosition() {
        return nonNull(this.calculated) && this.calculated instanceof Position
                ? (Position) this.calculated
                : getTarget().getPosition();
    }
}