package com.agutsul.chess.activity.impact;

import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceBatteryImpact<COLOR extends Color,
                                PIECE1 extends Piece<COLOR> & Capturable & Movable & Lineable,
                                PIECE2 extends Piece<COLOR> & Capturable & Movable & Lineable>
        extends AbstractTargetActivity<Impact.Type,PIECE1,PIECE2>
        implements Impact<PIECE1> {

    private final Line line;
    private Integer value;

    public PieceBatteryImpact(PIECE1 piece1, PIECE2 piece2, Line fullLine) {
        super(Impact.Type.BATTERY, piece1, piece2);
        this.line = fullLine;
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
        return getTarget().getPosition();
    }

    public Line getLine() {
        return this.line;
    }

    @Override
    public String toString() {
        return String.format("%s:%s&%s", getType(), getSource(), getTarget());
    }

    private Integer calculateValue() {
        return Stream.of(getSource(), getTarget())
                .mapToInt(Piece::getValue)
                .sum();
    }
}