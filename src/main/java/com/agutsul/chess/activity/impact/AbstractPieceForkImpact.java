package com.agutsul.chess.activity.impact;

import static org.apache.commons.lang3.StringUtils.join;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceForkImpact<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       ATTACKER extends Piece<COLOR1> & Capturable,
                                       ATTACKED extends Piece<COLOR2>,
                                       IMPACT extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,Collection<IMPACT>>
        implements PieceForkImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final Mode mode;

    AbstractPieceForkImpact(Mode mode, ATTACKER piece, Collection<IMPACT> impacts) {
        super(Impact.Type.FORK, piece, impacts);
        this.mode = mode;
    }

    @Override
    public final Integer getValue() {
        var value = Stream.of(getTarget())
                .flatMap(Collection::stream)
                .mapToInt(Impact::getValue)
                .sum();

        return PieceForkImpact.super.getValue() * Math.abs(value);
    }

    @Override
    public final Mode getMode() {
        return mode;
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public final String toString() {
        return String.format("%s:%s:%sx(%s)",
                getType(), getMode(), getSource(), join(getTarget(), ",")
        );
    }
}