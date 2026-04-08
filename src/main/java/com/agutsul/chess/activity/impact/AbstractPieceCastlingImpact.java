package com.agutsul.chess.activity.impact;

import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Castlingable.Side;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceCastlingImpact<COLOR  extends Color,
                                           PIECE1 extends Piece<COLOR> & Movable & Castlingable,
                                           PIECE2 extends Piece<COLOR> & Movable & Castlingable,
                                           SOURCE extends PieceMotionImpact<COLOR,PIECE1>,
                                           TARGET extends PieceMotionImpact<COLOR,PIECE2>>
        extends AbstractTargetActivity<Impact.Type,SOURCE,TARGET>
        implements Impact<SOURCE> {

    private final Side side;

    AbstractPieceCastlingImpact(Side side, SOURCE source, TARGET target) {
        super(Impact.Type.CASTLING, source, target);
        this.side = side;
    }

    public final Side getSide() {
        return side;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final SOURCE getSource() {
        return (SOURCE) getImpact(Piece.Type.KING);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final TARGET getTarget() {
        return (TARGET) getImpact(Piece.Type.ROOK);
    }

    @Override
    public final Integer getValue() {
        return Impact.super.getValue() * Math.abs(super.getSource().getValue() + super.getTarget().getValue());
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public final String toString() {
        return String.format("[%s] [%s]", getSource(), getTarget());
    }

    private PieceMotionImpact<COLOR,?> getImpact(Piece.Type pieceType) {
        return Stream.of(super.getSource(), super.getTarget())
                .filter(impact -> Objects.equals(impact.getSource().getType(), pieceType))
                .findFirst()
                .get();
    }
}