package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public final class PieceAbsoluteDiscoveredAttackImpact<COLOR1 extends Color,
                                                       COLOR2 extends Color,
                                                       PIECE  extends Piece<COLOR1> & Movable & Capturable,
                                                       ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                       ATTACKED extends KingPiece<COLOR2>,
                                                       SOURCE extends AbstractTargetActivity<Impact.Type,PIECE,?> & Impact<PIECE>>
        extends AbstractPieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                    SOURCE,
                                                    PieceCheckImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    @SuppressWarnings("unchecked")
    public PieceAbsoluteDiscoveredAttackImpact(PieceMotionImpact<COLOR1,PIECE> moveImpact,
                                               ATTACKER attacker, ATTACKED king, Line line) {

        this((SOURCE) moveImpact, new PieceCheckImpact<>(attacker, king, line, true));
    }

    @SuppressWarnings("unchecked")
    public PieceAbsoluteDiscoveredAttackImpact(AbstractPieceAttackImpact<COLOR1,COLOR2,PIECE,Piece<COLOR2>> attackImpact,
                                               ATTACKER attacker, ATTACKED king, Line line) {

        this((SOURCE) attackImpact, new PieceCheckImpact<>(attacker, king, line, true));
    }

    private PieceAbsoluteDiscoveredAttackImpact(SOURCE sourceImpact,
                                                PieceCheckImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> checkImpact) {

        super(Mode.ABSOLUTE, sourceImpact, checkImpact);
    }
}