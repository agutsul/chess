package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceMoveAction<COLOR extends Color,
                             PIECE extends Piece<COLOR> & Movable>
        extends AbstractMoveAction<COLOR,PIECE> {

    private static final Logger LOGGER = getLogger(PieceMoveAction.class);

    public PieceMoveAction(PIECE piece, Position position) {
        this(Action.Type.MOVE, piece, position);
    }

    PieceMoveAction(Action.Type type, PIECE piece, Position position) {
        super(type, piece, position);
    }

    @Override
    public void execute() {
        LOGGER.info("Executing move to '{}' by '{}'", getPosition(), getPiece());
        getPiece().move(getPosition());
    }

    @Override
    public final int compareTo(Action<?> action) {
        int compared = ObjectUtils.compare(action.getType(), getType());
        if (compared != 0) {
            return compared;
        }

        return ObjectUtils.compare(getPiece().getType(), action.getPiece().getType());
    }
}