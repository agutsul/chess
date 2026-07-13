package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceAbsoluteImpendingAttackImpact<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      ATTACKER extends Piece<COLOR1> & Movable & Capturable,
                                                      ATTACKED extends KingPiece<COLOR2>,
                                                      SOURCE extends AbstractTargetActivity<Impact.Type,ATTACKER,?> & Impact<ATTACKER>>
        extends AbstractPieceImpendingAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,SOURCE,
                                                   PieceCheckImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    @SuppressWarnings("unchecked")
    public PieceAbsoluteImpendingAttackImpact(ATTACKER attacker, Position position, ATTACKED attacked) {
        this((SOURCE) new PieceMotionImpact<>(attacker, position),
                // TODO: adjust attacker piece to properly implement calculateValue()
                // because it uses isProtected() based on current piece location.
                // It should calculate isProtected() for the new position ( simulating piece's move )
                new PieceCheckImpact<>(attacker, attacked)
        );
    }

    @SuppressWarnings("unchecked")
    public PieceAbsoluteImpendingAttackImpact(ATTACKER attacker, Piece<COLOR2> attacked, ATTACKED nextAttacked) {
        this((SOURCE) createAttackImpact(attacker, attacked),
                // TODO: adjust attacker piece to properly implement calculateValue()
                // because it uses isProtected() based on current piece location.
                // It should calculate isProtected() for the new position ( simulating piece's move )
                new PieceCheckImpact<>(attacker, nextAttacked)
        );
    }

    public PieceAbsoluteImpendingAttackImpact(SOURCE sourceImpact,
                                              PieceCheckImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> targetImpact) {

        super(Mode.ABSOLUTE, sourceImpact, targetImpact);
    }
}