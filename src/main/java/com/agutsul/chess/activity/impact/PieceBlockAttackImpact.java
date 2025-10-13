package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public final class PieceBlockAttackImpact<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          BLOCKER  extends Piece<COLOR1> & Movable,
                                          DEFENDED extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractTargetActivity<Impact.Type,BLOCKER,Position>
        implements PieceBlockImpact<COLOR1,COLOR2,BLOCKER,DEFENDED,ATTACKER> {

    private final AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DEFENDED> attackImpact;

    public PieceBlockAttackImpact(BLOCKER piece, Position position,
                                  AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DEFENDED> attackImpact) {

        super(Impact.Type.BLOCK, piece, position);
        this.attackImpact = attackImpact;
    }

    @Override
    public Position getPosition() {
        return getTarget();
    }

    @Override
    public Line getLine() {
        return attackImpact.getLine().get();
    }

    @Override
    public BLOCKER getBlocker() {
        return getSource();
    }

    @Override
    public DEFENDED getAttacked() {
        return attackImpact.getTarget();
    }

    @Override
    public ATTACKER getAttacker() {
        return attackImpact.getSource();
    }
}