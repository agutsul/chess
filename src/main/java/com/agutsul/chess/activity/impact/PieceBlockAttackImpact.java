package com.agutsul.chess.activity.impact;

import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceBlockAttackImpact<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          BLOCKER  extends Piece<COLOR1> & Movable,
                                          DEFENDED extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends AbstractTargetActivity<Impact.Type,BLOCKER,Position>
        implements PieceBlockImpact<COLOR1,COLOR2,BLOCKER,DEFENDED,ATTACKER> {

    private final AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DEFENDED> attackImpact;
    private Integer value;

    public PieceBlockAttackImpact(BLOCKER piece, Position position,
                                  AbstractPieceAttackImpact<COLOR2,COLOR1,ATTACKER,DEFENDED> attackImpact) {

        super(Impact.Type.BLOCK, piece, position);
        this.attackImpact = attackImpact;
    }

    @Override
    public Integer getValue() {
        if (this.value != null) {
            return this.value;
        }

        this.value = calculateValue();
        return this.value;
    }

    @Override
    public Position getPosition() {
        return getTarget();
    }

    @Override
    public Line getLine() {
        return Stream.of(attackImpact.getLine())
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(null);
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

    @Override
    public String toString() {
        return String.format("%s:%s (%s [%s] %s)",
                getType(), getBlocker(), getAttacker(), getPosition(), getAttacked()
        );
    }

    private Integer calculateValue() {
        var diff = Math.abs(getBlocker().getValue()) - Math.abs(getAttacked().getValue());
        var value = attackImpact.getValue() + Math.negateExact(getBlocker().getDirection()) * diff;
        return Math.negateExact(value);
    }
}