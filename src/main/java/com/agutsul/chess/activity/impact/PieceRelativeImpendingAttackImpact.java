package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;

import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceRelativeImpendingAttackImpact<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                                      ATTACKED extends Piece<COLOR2>,
                                                      SOURCE extends AbstractTargetActivity<Impact.Type,ATTACKER,?> & Impact<ATTACKER>>
        extends AbstractPieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,SOURCE,
                                                   PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    @SuppressWarnings("unchecked")
    public PieceRelativeImpendingAttackImpact(ATTACKER attacker, Position position, ATTACKED attacked) {
        this((SOURCE) new PieceMotionImpact<>(attacker, position),
                new PieceAttackImpact<>(attacker, attacked)
        );
    }

    @SuppressWarnings("unchecked")
    public PieceRelativeImpendingAttackImpact(ATTACKER attacker, Piece<COLOR2> attacked, ATTACKED nextAttacked) {
        this((SOURCE) createAttackImpact(attacker, attacked),
                new PieceAttackImpact<>(attacker, nextAttacked)
        );
    }

    public PieceRelativeImpendingAttackImpact(SOURCE sourceImpact,
                                              PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> targetImpact) {

        super(Mode.RELATIVE, sourceImpact, targetImpact);
    }

    @Override
    Integer calculateValue() {
        return Stream.of(getSource(), getTarget())
                .mapToInt(Impact::getValue)
                .sum();
    }
}