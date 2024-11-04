package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Demotable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;

public class CancelPromoteAction<COLOR1 extends Color,
                                 PIECE1 extends Piece<COLOR1> & Movable & Capturable & Demotable>
        extends AbstractPromoteAction<COLOR1,PIECE1> {

    private static final Logger LOGGER = getLogger(CancelPromoteAction.class);

    public CancelPromoteAction(CancelMoveAction<COLOR1,PIECE1> action) {
        super(action);
    }

    public <COLOR2 extends Color, PIECE2 extends Piece<COLOR2>>
            CancelPromoteAction(CancelCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> action) {
        super(action);
    }

    @Override
    public void execute() {
        var action = getSource();
        var piece = action.getSource();

        LOGGER.info("Cancel promote by '{}'", piece);

        // cancel promotion back to pawn
        piece.demote();

        // cancel origin action
        if (Action.Type.MOVE.equals(action.getType())
                || Action.Type.CAPTURE.equals(action.getType())) {

            action.execute();
        }
    }
}