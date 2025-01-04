package com.agutsul.chess.activity.action;

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
        extends AbstractTargetActivity<ACTION1,ACTION2>
        implements Action<ACTION1> {

    private final Castlingable.Side side;

    AbstractCastlingAction(Castlingable.Side side, ACTION1 sourceAction, ACTION2 targetAction) {
        super(Action.Type.CASTLING, sourceAction, targetAction);
        this.side = side;
    }

    @Override
    public String getCode() {
        return this.side.name();
    }

    public Castlingable.Side getSide() {
        return this.side;
    }

    @Override
    public Position getPosition() {
        return getKingCastlingAction().getPosition();
    }

    @Override
    public String toString() {
        return getCode();
    }

    // returns king related part of castling action
    // potentially king can be in both source and target sub-actions
    // king related part allows to identify what kind of castling it is
    AbstractMoveAction<?,?> getKingCastlingAction() {
        return Stream.of(getSource(), getTarget())
                .filter(action -> Piece.Type.KING.equals(action.getSource().getType()))
                .findFirst()
                .get();
    }
}