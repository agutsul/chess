package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.piece.Piece.isKing;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.AbstractSourceActivity;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceXRayImpact<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                       TARGET extends Piece<?>,
                                       IMPACT extends AbstractTargetActivity<Impact.Type,SOURCE,TARGET> & Impact<SOURCE>>
        extends AbstractSourceActivity<Impact.Type,AbstractTargetActivity<Impact.Type,SOURCE,TARGET>>
        implements PieceXRayImpact<COLOR1,COLOR2,SOURCE,TARGET> {

    private final Mode mode;
    private final Collection<Piece<?>> pieces;
    private Integer value;

    AbstractPieceXRayImpact(IMPACT impact, Collection<Piece<?>> pieces) {
        super(Impact.Type.XRAY, impact);

        this.mode = createMode(impact.getTarget());
        this.pieces = pieces;
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
    @SuppressWarnings("unchecked")
    public final IMPACT getSource() {
        return (IMPACT) super.getSource();
    }

    @Override
    public final SOURCE getPiece() {
        return getSource().getSource();
    }

    @Override
    public final TARGET getTarget() {
        return getSource().getTarget();
    }

    @Override
    public final Position getPosition() {
        return getTarget().getPosition();
    }

    @Override
    public final Mode getMode() {
        return mode;
    }

    @Override
    public final Collection<Piece<?>> getPieces() {
        return unmodifiableCollection(pieces);
    }

    @Override
    public final String toString() {
        return String.format("%s:%s:%s", getType(), getMode(), getSource());
    }

    final int getPieceValues(Color color) {
        return Stream.of(pieces)
                .flatMap(Collection::stream)
                .filter(piece -> Objects.equals(piece.getColor(), color))
                .mapToInt(Piece::getValue)
                .sum();
    }

    Integer calculateValue() {
        return getSource().getValue();
    }

    private static Mode createMode(Piece<?> piece) {
        return isKing(piece) ? Mode.ABSOLUTE : Mode.RELATIVE;
    }
}