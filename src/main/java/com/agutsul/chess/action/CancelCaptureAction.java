package com.agutsul.chess.action;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;

public class CancelCaptureAction<COLOR1 extends Color,
                                 COLOR2 extends Color,
                                 PIECE1 extends Piece<COLOR1> & Capturable,
                                 PIECE2 extends Piece<COLOR2> & Capturable>
        extends AbstractCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> {

    private static final Logger LOGGER = getLogger(CancelCaptureAction.class);

    public CancelCaptureAction(PIECE1 predator, PIECE2 victim) {
        super(Type.CAPTURE, predator, victim);
    }

    @Override
    public void execute() {
        LOGGER.info("Cancel capturing '{}' by '{}'", getTarget(), getSource());
        getSource().uncapture(getTarget());
    }
}