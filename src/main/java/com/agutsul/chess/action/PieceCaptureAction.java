package com.agutsul.chess.action;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public class PieceCaptureAction<COLOR1 extends Color,
                                COLOR2 extends Color,
                                PIECE1 extends Piece<COLOR1> & Capturable,
                                PIECE2 extends Piece<COLOR2>>
        extends AbstractCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> {

    private static final Logger LOGGER = getLogger(PieceCaptureAction.class);

    protected static final Line EMPTY_LINE = new Line(emptyList());

    private final Line attackLine;

    public PieceCaptureAction(PIECE1 predator, PIECE2 victim) {
        this(Action.Type.CAPTURE, predator, victim, EMPTY_LINE);
    }

    public PieceCaptureAction(PIECE1 predator, PIECE2 victim, Line attackLine) {
        this(Action.Type.CAPTURE, predator, victim, attackLine);
    }

    PieceCaptureAction(Type type, PIECE1 predator, PIECE2 victim, Line attackLine) {
        super(type, predator, victim);
        this.attackLine = attackLine;
    }

    public Line getAttackLine() {
        return this.attackLine;
    }

    @Override
    public void execute() {
        LOGGER.info("Executing capturing '{}' by '{}'", getTarget(), getSource());
        getSource().capture(getTarget());
    }
}