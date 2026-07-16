package com.agutsul.chess.activity.impact;

import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceImpendingAttackImpact<COLOR1 extends Color,
                                                  COLOR2 extends Color,
                                                  ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                                  ATTACKED extends Piece<COLOR2>,
                                                  SOURCE extends AbstractTargetActivity<Impact.Type,ATTACKER,?> & Impact<ATTACKER>,
                                                  TARGET extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractTargetActivity<Impact.Type,
                                       AbstractTargetActivity<Impact.Type,ATTACKER,?>,
                                       TARGET>
        implements PieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final Mode mode;
    private Integer value;

    AbstractPieceImpendingAttackImpact(Mode mode, SOURCE sourceImpact, TARGET impendingAttack) {
        super(Impact.Type.IMPENDING_ATTACK, sourceImpact, impendingAttack);
        this.mode = mode;
    }

    abstract Integer calculateValue();

    @Override
    public final Integer getValue() {
        if (this.value != null) {
            return this.value;
        }

        this.value = calculateValue();
        return this.value;
    }

    @Override
    public final Mode getMode() {
        return mode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final SOURCE getSource() {
        return (SOURCE) super.getSource();
    }

    @Override
    public final TARGET getTarget() {
        return super.getTarget();
    }

    @Override
    public final ATTACKER getAttacker() {
        return getSource().getSource();
    }

    @Override
    public final ATTACKED getAttacked() {
        return getTarget().getTarget();
    }

    @Override
    public final Position getPosition() {
        // next position used as source to attack target piece
        return getSource().getPosition();
    }

    @Override
    public final Optional<Line> getLine() {
        return getTarget().getLine();
    }

    @Override
    public final String toString() {
        return String.format("%s:%s:(%s)x%s",
                getType(), getMode(), getSource(), getAttacked()
        );
    }
}