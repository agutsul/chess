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

public final class PieceInterferenceProtectImpact<COLOR1 extends Color,
                                                  COLOR2 extends Color,
                                                  PIECE  extends Piece<COLOR1> & Movable,
                                                  PROTECTOR extends Piece<COLOR2> & Capturable & Lineable,
                                                  PROTECTED extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,PIECE,Position>
        implements PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED> {

    private final PieceProtectImpact<COLOR2,PROTECTOR,PROTECTED> protectImpact;
    private Integer value;

    public PieceInterferenceProtectImpact(PIECE source, Position target,
                                          PieceProtectImpact<COLOR2,PROTECTOR,PROTECTED> impact) {

        super(Impact.Type.INTERFERENCE, source, target);
        this.protectImpact = impact;
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
        return Stream.of(protectImpact.getLine())
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(null);
    }

    @Override
    public PIECE getInterferencor() {
        return getSource();
    }

    @Override
    public PROTECTED getProtected() {
        return protectImpact.getTarget();
    }

    @Override
    public PROTECTOR getProtector() {
        return protectImpact.getSource();
    }

    @Override
    public String toString() {
        return String.format("%s:%s %s %s",
                getType(), getProtector(), getInterferencor(), getProtected()
        );
    }

    private Integer calculateValue() {
        return getSource().getValue() + Math.negateExact(protectImpact.getValue());
    }
}