package com.agutsul.chess.activity.impact;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceDesperadoAttackImpact<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              DESPERADO extends Piece<COLOR1> & Capturable,
                                              ATTACKER  extends Piece<COLOR2> & Capturable,
                                              ATTACKED  extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,
                                       AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED>,
                                       AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DESPERADO>>
        implements PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                        AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED>> {

    private final Mode mode;
    private Integer value;

    public PieceDesperadoAttackImpact(Mode mode,
                                      AbstractPieceAttackImpact<COLOR1,COLOR2,DESPERADO,ATTACKED> source,
                                      AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DESPERADO> target) {

        super(Impact.Type.DESPERADO, source, target);
        this.mode = mode;
    }

    @Override
    public Integer getValue() {
        if (this.value != null) {
            return this.value;
        }

        this.value = calculateValue();
        return this.value;
    }

    @Override
    public ATTACKER getAttacker() {
        return nonNull(getTarget())
                ? getTarget().getSource()
                : null;
    }

    @Override
    public ATTACKED getAttacked() {
        return getSource().getTarget();
    }

    @Override
    public DESPERADO getDesperado() {
        return getSource().getSource();
    }

    @Override
    public Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public String toString() {
        return String.format("%s:%s:[ %s ] => [ %s ]",
                getType(), getMode(), getSource(), isNull(getTarget()) ? EMPTY : getTarget()
        );
    }

    @Override
    public Mode getMode() {
        return this.mode;
    }

    private Integer calculateValue() {
        return Stream.of(getSource(), getTarget())
                .filter(Objects::nonNull)
                .mapToInt(Impact::getValue)
                .sum();
    }
}