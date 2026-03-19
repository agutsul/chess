package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class PieceRelativeDiscoveredAttackImpact<COLOR1 extends Color,
                                                       COLOR2 extends Color,
                                                       PIECE  extends Piece<COLOR1> & Movable & Capturable,
                                                       ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                       ATTACKED extends Piece<COLOR2>,
                                                       IMPACT extends AbstractTargetActivity<Impact.Type,PIECE,?> & Impact<PIECE>>
        extends AbstractPieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                    IMPACT,
                                                    PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    @SuppressWarnings("unchecked")
    public PieceRelativeDiscoveredAttackImpact(PieceMotionImpact<COLOR1,PIECE> moveImpact,
                                               ATTACKER attacker, ATTACKED attacked, Line line) {

        this((IMPACT) moveImpact, new PieceAttackImpact<>(attacker, attacked, line, true));
    }

    @SuppressWarnings("unchecked")
    public PieceRelativeDiscoveredAttackImpact(AbstractPieceAttackImpact<COLOR1,COLOR2,PIECE,Piece<COLOR2>> attackImpact,
                                               ATTACKER attacker, ATTACKED attacked, Line line) {

        this((IMPACT) attackImpact, new PieceAttackImpact<>(attacker, attacked, line, true));
    }

    private PieceRelativeDiscoveredAttackImpact(IMPACT sourceImpact,
                                                PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> checkImpact) {

        super(Mode.RELATIVE, sourceImpact, checkImpact);
    }
}