package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public final class PieceBlockAttackImpact<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          BLOCKER extends Piece<COLOR1>,
                                          DEFENDED extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractTargetActivity<Impact.Type,BLOCKER,ATTACKER>
        implements PieceBlockImpact<COLOR1,COLOR2,BLOCKER,DEFENDED,ATTACKER> {

    private final DEFENDED defended;
    private final Line line;
    private final Position position;

    public PieceBlockAttackImpact(BLOCKER blocker, ATTACKER attacker, DEFENDED defended,
                                  Line blockedLine, Position blockPosition) {

        super(Impact.Type.BLOCK, blocker, attacker);

        this.defended = defended;
        this.line = blockedLine;
        this.position = blockPosition;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Line getLine() {
        return this.line;
    }

    @Override
    public BLOCKER getBlocker() {
        return getSource();
    }

    @Override
    public DEFENDED getDefended() {
        return this.defended;
    }

    @Override
    public ATTACKER getAttacker() {
        return getTarget();
    }
}