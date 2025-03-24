package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class CancelMoveAction<COLOR extends Color,
                              PIECE extends Piece<COLOR> & Movable>
        extends AbstractMoveAction<COLOR,PIECE> {

    private static final Logger LOGGER = getLogger(CancelMoveAction.class);

    public CancelMoveAction(Action.Type type, PIECE piece, Position position) {
        super(type, piece, position);
    }

    @Override
    public void execute() {
        LOGGER.info("Cancel move to '{}' by '{}'", getPosition(), getPiece());
        getPiece().unmove(getPosition());
    }
}