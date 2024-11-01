package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.CancelCastlingAction.UncastlingMoveAction;
import com.agutsul.chess.piece.Castlingable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class CancelCastlingAction<COLOR extends Color,
                                  PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                                  PIECE2 extends Piece<COLOR> & Castlingable & Movable>
        extends AbstractCastlingAction<COLOR,
                                       PIECE1,
                                       PIECE2,
                                       UncastlingMoveAction<COLOR,PIECE1>,
                                       UncastlingMoveAction<COLOR,PIECE2>> {

    private static final Logger LOGGER = getLogger(CancelCastlingAction.class);

    public CancelCastlingAction(String code,
                                UncastlingMoveAction<COLOR,PIECE1> sourceAction,
                                UncastlingMoveAction<COLOR,PIECE2> targetAction) {
        super(code, sourceAction, targetAction);
    }

    @Override
    public void execute() {
        LOGGER.info("Cancel castling '{}'", this);

        getSource().execute();
        getTarget().execute();
    }

    public static final class UncastlingMoveAction<COLOR extends Color,
                                                   PIECE extends Piece<COLOR> & Castlingable & Movable>
            extends CancelMoveAction<COLOR,PIECE> {

        public UncastlingMoveAction(PIECE piece, Position position) {
            super(piece, position);
        }

        @Override
        public void execute() {
            getSource().uncastling(getPosition());
        }
    }
}