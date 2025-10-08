package com.agutsul.chess.activity.impact;

import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceUnderminingImpact<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              ATTACKER extends Piece<COLOR1> & Capturable,
                                              ATTACKED extends Piece<COLOR2>,
                                              IMPACT extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,ATTACKED>
        implements PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final IMPACT impact;

    AbstractPieceUnderminingImpact(IMPACT impact) {
        super(Impact.Type.UNDERMINING, impact.getSource(), impact.getTarget());
        this.impact = impact;
    }

    @Override
    public final ATTACKER getAttacker() {
        return getSource();
    }

    @Override
    public final ATTACKED getAttacked() {
        return getTarget();
    }

    @Override
    public final Optional<Line> getLine() {
        return impact.getLine();
    }

    @Override
    public final Position getPosition() {
        return impact.getPosition();
    }

    @Override
    public final String toString() {
        return String.format("%s_X_%s", getAttacker(), getAttacked());
    }
}