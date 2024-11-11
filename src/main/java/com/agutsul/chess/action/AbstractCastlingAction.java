package com.agutsul.chess.action;

import java.util.stream.Stream;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Castlingable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractCastlingAction<COLOR extends Color,
                                             PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                                             PIECE2 extends Piece<COLOR> & Castlingable & Movable,
                                             ACTION1 extends AbstractMoveAction<COLOR,PIECE1>,
                                             ACTION2 extends AbstractMoveAction<COLOR,PIECE2>>
        extends AbstractTargetAction<ACTION1,ACTION2> {

    private final String code;

    AbstractCastlingAction(String code, ACTION1 sourceAction, ACTION2 targetAction) {
        super(Type.CASTLING, sourceAction, targetAction);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    // returns king's target position
    public Position getPosition() {
        return getKingCastlingAction().getPosition();
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