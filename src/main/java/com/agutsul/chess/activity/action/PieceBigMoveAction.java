package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

public class PieceBigMoveAction<COLOR extends Color,
                                PIECE extends PawnPiece<COLOR>>
        extends PieceMoveAction<COLOR,PIECE> {

    private static final Logger LOGGER = getLogger(PieceBigMoveAction.class);

    public PieceBigMoveAction(PIECE piece, Position position) {
        super(Action.Type.BIG_MOVE, piece, position);
    }

    @Override
    public final void execute() {
        LOGGER.info("Executing big move to '{}' by '{}'", getPosition(), getPiece());
        super.execute();
    }
}