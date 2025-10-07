package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceDiscoveredAttackImpact<COLOR1 extends Color,
                                                   COLOR2 extends Color,
                                                   PIECE extends Piece<COLOR1>,
                                                   ATTACKER extends Piece<COLOR1> & Capturable,
                                                   ATTACKED extends Piece<COLOR2>,
                                                   IMPACT extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractTargetActivity<Impact.Type,PIECE,IMPACT>
        implements PieceDiscoveredAttackImpact<COLOR1,PIECE> {

    private final Mode mode;

    AbstractPieceDiscoveredAttackImpact(Mode mode, PIECE piece, IMPACT discoveredAttack) {
        super(Impact.Type.ATTACK, piece, discoveredAttack);
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