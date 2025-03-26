package com.agutsul.chess.activity.action;

import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractCastlingAction<COLOR extends Color,
                                             PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                                             PIECE2 extends Piece<COLOR> & Castlingable & Movable,
                                             ACTION1 extends AbstractMoveAction<COLOR,PIECE1>,
                                             ACTION2 extends AbstractMoveAction<COLOR,PIECE2>>
        extends AbstractTargetActivity<Action.Type,ACTION1,ACTION2>
        implements Action<ACTION1> {

    private final Castlingable.Side side;

    AbstractCastlingAction(Castlingable.Side side, ACTION1 sourceAction, ACTION2 targetAction) {
        super(Action.Type.CASTLING, sourceAction, targetAction);
        this.side = side;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final PIECE1 getPiece() {
        return getSource().getPiece();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final ACTION1 getSource() {
        return (ACTION1) getAction(Piece.Type.KING);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final ACTION2 getTarget() {
        return (ACTION2) getAction(Piece.Type.ROOK);
    }

    @Override
    public final boolean matches(Piece<?> piece, Position position) {
        return getSource().matches(piece, position)
                || getTarget().matches(piece, position);
    }

    @Override
    public final String getCode() {
        return this.side.name();
    }

    public final Castlingable.Side getSide() {
        return this.side;
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public final String toString() {
        return getCode();
    }

    private AbstractMoveAction<?,?> getAction(Piece.Type pieceType) {
        return Stream.of(super.getSource(), super.getTarget())
                .filter(action -> Objects.equals(action.getPiece().getType(), pieceType))
                .findFirst()
                .get();
    }
}