package com.agutsul.chess.piece;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.piece.state.AbstractPieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

class ActivePieceState<PIECE extends Piece<Color> & Movable & Capturable>
        extends AbstractPieceState<PIECE> {

    private static final Logger LOGGER = getLogger(ActivePieceState.class);

    private final Rule<Piece<Color>, Collection<Action<?>>> actionRule;
    private final Rule<Piece<Color>, Collection<Impact<?>>> impactRule;

    protected final Board board;

    ActivePieceState(Board board,
                     Rule<Piece<Color>, Collection<Action<?>>> actionRule,
                     Rule<Piece<Color>, Collection<Impact<?>>> impactRule) {

        super(Type.ACTIVE);

        this.board = board;
        this.actionRule = actionRule;
        this.impactRule = impactRule;
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece) {
        LOGGER.info("Calculate '{}' actions", piece);
        return actionRule.evaluate(piece);
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece) {
        LOGGER.info("Calculate '{}' impacts", piece);
        return impactRule.evaluate(piece);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void move(PIECE piece, Position position) {
        LOGGER.info("Move '{}' to '{}'", piece, position);

        var possibleMoves = board.getActions(piece, PieceMoveAction.class).stream()
                .map(PieceMoveAction::getTarget)
                .collect(toSet());

        if (!possibleMoves.contains(position)) {
            throw new IllegalActionException(
                String.format("%s invalid move to %s", piece, position)
            );
        }

        ((AbstractPiece<Color>) piece).doMove(position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void capture(PIECE piece, Piece<?> targetPiece) {
        LOGGER.info("Capture '{}' by '{}'", targetPiece, piece);

        var possibleCaptures = board.getActions(piece, PieceCaptureAction.class).stream()
                .map(PieceCaptureAction::getTarget)
                .collect(toSet());

        if (!possibleCaptures.contains(targetPiece)) {
            throw new IllegalActionException(
                String.format("%s invalid capture of %s", piece, targetPiece)
            );
        }

        ((AbstractPiece<Color>) piece).doCapture(targetPiece);
    }
}