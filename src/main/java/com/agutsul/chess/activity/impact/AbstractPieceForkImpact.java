package com.agutsul.chess.activity.impact;

import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Protectable;
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
    private Integer value;

    AbstractPieceForkImpact(Mode mode, ATTACKER piece, Collection<IMPACT> impacts) {
        super(Impact.Type.FORK, piece, impacts);
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

    @Override
    public final Integer getValue() {
        if (this.value != null) {
            return this.value;
        }

        this.value = calculateValue();
        return this.value;
    }

    @Override
    public final Collection<ATTACKED> getAttacked() {
        var pieces = Stream.of(getTarget())
                .flatMap(Collection::stream)
                .map(AbstractPieceAttackImpact::getTarget)
                .sorted(comparing(
                        Piece::getType,
                        (type1,type2) -> Integer.compare(type2.rank(), type1.rank())
                ))
                .toList();

        return pieces;
    }

    @Override
    public final String toString() {
        return String.format("%s:%s:%sx(%s)",
                getType(), getMode(), getSource(), join(getTarget(), ",")
        );
    }

    private Integer calculateValue() {
        var value = Stream.of(getTarget())
                .flatMap(Collection::stream)
                .mapToInt(Impact::getValue)
                .sum();

        return ((Protectable) getSource()).isProtected()
                ? value
                : value + Math.negateExact(getSource().getValue());
    }
}