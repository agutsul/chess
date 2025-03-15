package com.agutsul.chess.activity.action;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public class PieceCaptureAction<COLOR1 extends Color,
                                COLOR2 extends Color,
                                PIECE1 extends Piece<COLOR1> & Capturable,
                                PIECE2 extends Piece<COLOR2>>
        extends AbstractCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> {

    private static final Logger LOGGER = getLogger(PieceCaptureAction.class);

    private Line line;

    public PieceCaptureAction(PIECE1 predator, PIECE2 victim) {
        this(predator, victim, null);
    }

    public PieceCaptureAction(PIECE1 predator, PIECE2 victim, Line attackLine) {
        super(Action.Type.CAPTURE, predator, victim);
        this.line = attackLine;
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(this.line);
    }

    @Override
    public final void execute() {
        LOGGER.info("Executing capturing '{}' by '{}'", getTarget(), getSource());
        getSource().capture(getTarget());
    }
}