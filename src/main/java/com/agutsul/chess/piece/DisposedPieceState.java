package com.agutsul.chess.piece;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.position.Position;

class DisposedPieceState<PIECE extends Piece<Color> & Movable & Capturable>
        extends AbstractPieceState<PIECE> {

    private static final Logger LOGGER = getLogger(DisposedPieceState.class);

    DisposedPieceState() {
        super(Type.INACTIVE);
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece) {
        LOGGER.info("Calculate '{}' actions", piece);
        return emptyList();
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece) {
        LOGGER.info("Calculate '{}' impacts", piece);
        return emptyList();
    }

    @Override
    public void move(PIECE piece, Position position) {
        LOGGER.info("Move '{}' to '{}'", piece, position);
        // do nothing because piece is disposed
    }

    @Override
    public void capture(PIECE piece, Piece<?> targetPiece) {
        LOGGER.info("Capture '{}' by '{}'", targetPiece, piece);
        // do nothing because piece is disposed
    }
}