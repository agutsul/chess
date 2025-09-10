package com.agutsul.chess.activity.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceForkImpact<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       ATTACKER extends Piece<COLOR1> & Capturable,
                                       FORKED extends Piece<COLOR2>,
                                       IMPACT extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,FORKED>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,Collection<IMPACT>>
        implements PieceForkImpact<COLOR1,COLOR2,ATTACKER,FORKED> {

    private final Mode mode;

    AbstractPieceForkImpact(Mode mode, ATTACKER piece, Collection<IMPACT> impacts) {
        super(Impact.Type.FORK, piece, impacts);
        this.mode = mode;
    }

    @Override
    public final Mode getMode() {
        return mode;
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }
}