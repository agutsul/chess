package com.agutsul.chess.activity.impact;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceRelativeDesperadoImpact<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                DESPERADO extends Piece<COLOR1> & Capturable,
                                                ATTACKER  extends Piece<COLOR2> & Capturable,
                                                ATTACKED  extends Piece<COLOR2>,
                                                IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
        extends AbstractTargetActivity<Impact.Type,IMPACT,Collection<IMPACT>>
        implements PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,IMPACT> {

    private Integer value;

    public PieceRelativeDesperadoImpact(IMPACT source, IMPACT target) {
        super(Impact.Type.DESPERADO, source, List.of(source, target));
    }

    @Override
    public Mode getMode() {
        return Mode.RELATIVE;
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
        return String.format("%s:%s:{%s%s%s}",
                getType(), getMode(),
                lineSeparator(),
                Stream.of(getTarget())
                    .flatMap(Collection::stream)
                    .map(String::valueOf)
                    .collect(joining(lineSeparator())),
                lineSeparator()
        );
    }

    private Integer calculateValue() {
        return Stream.of(getTarget())
                .flatMap(Collection::stream)
                .mapToInt(Impact::getValue)
                .sum();
    }
}