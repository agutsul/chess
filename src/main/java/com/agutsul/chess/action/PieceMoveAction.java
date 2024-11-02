package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceMoveAction<COLOR extends Color,
                             PIECE extends Piece<COLOR> & Movable>
        extends AbstractMoveAction<COLOR, PIECE> {

    private static final Logger LOGGER = getLogger(PieceMoveAction.class);

    public PieceMoveAction(PIECE piece, Position position) {
        super(piece, position);
    }

    @Override
    public void execute() {
        LOGGER.info("Executing move to '{}' by '{}'", getTarget(), getSource());
        getSource().move(getPosition());
    }
}