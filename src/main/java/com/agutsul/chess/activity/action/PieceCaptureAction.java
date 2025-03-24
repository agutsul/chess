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
        super(Action.Type.CAPTURE, predator, victim);
    }

    public PieceCaptureAction(PIECE1 predator, PIECE2 victim, Line attackLine) {
        this(predator, victim);
        this.line = attackLine;
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(this.line);
    }

    @Override
    public final void execute() {
        LOGGER.info("Executing capturing '{}' by '{}'", getTarget(), getPiece());
        getPiece().capture(getTarget());
    }

    @Override
    public final int compareTo(Action<?> action) {
        int compared = super.compareTo(action);
        if (compared != 0) {
            return compared;
        }

        // return action attacking 'the strongest' piece first
        return Integer.compare(getTargetRank(this), getTargetRank(action));
    }

    private static int getTargetRank(Action<?> action) {
        var captureAction = (PieceCaptureAction<?,?,?,?>) action;

        var targetPiece = captureAction.getTarget();
        var pieceType = targetPiece.getType();

        return pieceType.rank();
    }
}