package com.agutsul.chess.action;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public class PieceCaptureAction<COLOR1 extends Color,
                                COLOR2 extends Color,
                                PIECE1 extends Piece<COLOR1> & Capturable,
                                PIECE2 extends Piece<COLOR2> & Capturable>
        extends AbstractTargetAction<PIECE1, PIECE2> {

    private static final Logger LOGGER = getLogger(PieceCaptureAction.class);

    protected static final Line EMPTY_LINE = new Line(emptyList());

    private final Line attackLine;

    public PieceCaptureAction(PIECE1 piece1, PIECE2 piece2) {
        this(Action.Type.CAPTURE, piece1, piece2, EMPTY_LINE);
    }

    public PieceCaptureAction(PIECE1 piece1, PIECE2 piece2, Line attackLine) {
        this(Action.Type.CAPTURE, piece1, piece2, attackLine);
    }

    PieceCaptureAction(Type type, PIECE1 piece1, PIECE2 piece2, Line attackLine) {
        super(type, piece1, piece2);
        this.attackLine = attackLine;
    }

    @Override
    public void execute() {
        LOGGER.info("Executing capturing '{}' by '{}'", getTarget(), getSource());
        getSource().capture(getTarget());
    }

    @Override
    public Position getPosition() {
        return getTarget().getPosition();
    }

    public Line getAttackLine() {
        return new Line(attackLine);
    }

    @Override
    public String getCode() {
        return String.format("%sx%s", getSource(), createTargetLabel(getTarget()));
    }

    protected String createTargetLabel(Piece<?> targetPiece) {
        return createTargetLabel(targetPiece.getPosition());
    }

    protected String createTargetLabel(Position position) {
        return String.valueOf(position);
    }
}