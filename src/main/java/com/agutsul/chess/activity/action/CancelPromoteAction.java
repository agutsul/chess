package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.EnumSet;
import java.util.Set;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Executable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class CancelPromoteAction<COLOR1 extends Color,
                                       PIECE1 extends Piece<COLOR1> & Movable & Capturable & Demotable>
        extends AbstractPromoteAction<COLOR1,PIECE1> {

    private static final Logger LOGGER = getLogger(CancelPromoteAction.class);

    private static final Set<Action.Type> PROMOTABLE_TYPES =
            EnumSet.of(Action.Type.CAPTURE, Action.Type.MOVE);

    public CancelPromoteAction(CancelMoveAction<COLOR1,PIECE1> action) {
        super(action);
    }

    public <COLOR2 extends Color,PIECE2 extends Piece<COLOR2>>
            CancelPromoteAction(CancelCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> action) {
        super(action);
    }

    @Override
    public void execute() {
        var action = getSource();

        if (!PROMOTABLE_TYPES.contains(action.getType())) {
            throw new IllegalStateException(String.format(
                    "Unable to cancel promotion. Unsuppoted action type: '%s'",
                    action.getType()
            ));
        }

        var promoted = action.getSource();
        LOGGER.info("Cancel promote by '{}'", promoted);

        // cancel promotion back to pawn
        promoted.demote();
        // cancel origin action
        ((Executable) action).execute();
    }
}