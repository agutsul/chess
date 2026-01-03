package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.piece.Piece.isKing;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

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
                                       IMPACT extends AbstractTargetActivity<Impact.Type,SOURCE,TARGET>>
        extends AbstractSourceActivity<Impact.Type,AbstractTargetActivity<Impact.Type,SOURCE,TARGET>>
        implements PieceXRayImpact<COLOR1,COLOR2,SOURCE,TARGET> {

    private final Mode mode;
    private final Collection<Piece<?>> pieces;

    AbstractPieceXRayImpact(IMPACT impact, Collection<Piece<?>> pieces) {
        super(Impact.Type.XRAY, impact);

        this.mode = createMode(impact.getTarget());
        this.pieces = pieces;
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

    private static Mode createMode(Piece<?> piece) {
        return isKing(piece) ? Mode.ABSOLUTE : Mode.RELATIVE;
    }
}