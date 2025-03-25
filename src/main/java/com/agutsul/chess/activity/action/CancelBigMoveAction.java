package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class CancelBigMoveAction<COLOR extends Color,
                                       PIECE extends Piece<COLOR> & Movable>
        extends CancelMoveAction<COLOR,PIECE> {

    private static final Logger LOGGER = getLogger(CancelBigMoveAction.class);

    public CancelBigMoveAction(PIECE piece, Position position) {
        super(Action.Type.BIG_MOVE, piece, position);
    }

    @Override
    public void execute() {
        LOGGER.info("Cancel big move to '{}' by '{}'", getPosition(), getPiece());
        super.execute();
    }
}