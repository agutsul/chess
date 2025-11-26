package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceAbsoluteDesperadoImpact<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                DESPERADO extends Piece<COLOR1> & Capturable,
                                                ATTACKER  extends Piece<COLOR2> & Capturable,
                                                ATTACKED  extends Piece<COLOR2>,
                                                IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
        extends AbstractSourceActivity<Impact.Type,IMPACT>
        implements PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,IMPACT> {

    public PieceAbsoluteDesperadoImpact(IMPACT source) {
        super(Impact.Type.DESPERADO, source);
    }

    @Override
    public Mode getMode() {
        return Mode.ABSOLUTE;
    }

    @Override
    public ATTACKER getAttacker() {
        return getSource().getAttacker();
    }

    @Override
    public ATTACKED getAttacked() {
        return getSource().getAttacked();
    }

    @Override
    public DESPERADO getDesperado() {
        return getSource().getDesperado();
    }

    @Override
    public Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public String toString() {
        return String.valueOf(getSource());
    }
}