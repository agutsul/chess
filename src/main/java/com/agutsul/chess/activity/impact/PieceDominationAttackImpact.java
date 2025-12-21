package com.agutsul.chess.activity.impact;

import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceDominationAttackImpact<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         ATTACKER extends Piece<COLOR1> & Capturable,
                                         ATTACKED extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,ATTACKED>
        implements PieceDominationImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> attackImpact;

    public PieceDominationAttackImpact(AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> attackImpact) {
        super(Impact.Type.DOMINATION, attackImpact.getSource(), attackImpact.getTarget());
        this.attackImpact = attackImpact;
    }

    @Override
    public Position getPosition() {
        return this.attackImpact.getPosition();
    }

    @Override
    public ATTACKER getAttacker() {
        return getSource();
    }

    @Override
    public ATTACKED getAttacked() {
        return getTarget();
    }

    @Override
    public Optional<Line> getLine() {
        return this.attackImpact.getLine();
    }
}