package com.agutsul.chess.piece.impl;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.AbstractMoveAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.state.ActivePieceState;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.Rule;

final class ActivePieceStateImpl<PIECE extends Piece<?> & Movable & Capturable>
        extends AbstractPieceState<PIECE>
        implements ActivePieceState<PIECE> {

    private static final Logger LOGGER = getLogger(ActivePieceStateImpl.class);

    private final AbstractPieceRule<Action<?>,Action.Type> actionRule;
    private final AbstractPieceRule<Impact<?>,Impact.Type> impactRule;

    protected final Board board;

    @SuppressWarnings("unchecked")
    ActivePieceStateImpl(Board board,
                         Rule<Piece<?>,Collection<Action<?>>> actionRule,
                         Rule<Piece<?>,Collection<Impact<?>>> impactRule) {

        super(Type.ACTIVE);

        this.board = board;
        this.actionRule = (AbstractPieceRule<Action<?>,Action.Type>) actionRule;
        this.impactRule = (AbstractPieceRule<Impact<?>,Impact.Type>) impactRule;
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece) {
        LOGGER.info("Calculate '{}' actions", piece);
        return actionRule.evaluate(piece);
    }

    @Override
    public Collection<Action<?>> calculateActions(PIECE piece, Action.Type actionType) {
        LOGGER.info("Calculate '{}' actions ({})", piece, actionType.name());
        return actionRule.evaluate(piece, actionType);
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece) {
        LOGGER.info("Calculate '{}' impacts", piece);
        return impactRule.evaluate(piece);
    }

    @Override
    public Collection<Impact<?>> calculateImpacts(PIECE piece, Impact.Type impactType) {
        LOGGER.info("Calculate '{}' impacts ({})", piece, impactType.name());
        return impactRule.evaluate(piece, impactType);
    }

    @Override
    public void move(PIECE piece, Position position) {
        LOGGER.info("Move '{}' to '{}'", piece, position);

        var actions = board.getActions(piece, Action.Type.MOVE);
        var possiblePositions = actions.stream()
                .map(action -> (AbstractMoveAction<?,?>) action)
                .map(AbstractMoveAction::getTarget)
                .collect(toSet());

        if (!possiblePositions.contains(position)) {
            throw new IllegalActionException(
                    String.format("%s invalid move to %s", piece, position)
            );
        }

        ((AbstractPiece<?>) piece).doMove(position);
    }

    @Override
    public void capture(PIECE piece, Piece<?> targetPiece) {
        LOGGER.info("Capture '{}' by '{}'", targetPiece, piece);

        var possibleActions = board.getActions(piece, Action.Type.CAPTURE);
        var possiblePieces = possibleActions.stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .map(AbstractCaptureAction::getTarget)
                .collect(toSet());

        if (!possiblePieces.contains(targetPiece)) {
            throw new IllegalActionException(
                    String.format("%s invalid capture of %s", piece, targetPiece)
            );
        }

        ((AbstractPiece<?>) piece).doCapture(targetPiece);
    }
}