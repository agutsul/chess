package com.agutsul.chess.piece;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.action.AbstractCaptureAction;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.Rule;

class ActivePieceState<COLOR extends Color,
                       PIECE extends Piece<COLOR> & Movable & Capturable>
        extends AbstractPieceState<COLOR,PIECE> {

    private static final Logger LOGGER = getLogger(ActivePieceState.class);

    private final AbstractPieceRule<Action<?>,Action.Type> actionRule;
    private final AbstractPieceRule<Impact<?>,Impact.Type> impactRule;

    protected final Board board;

    ActivePieceState(Board board,
                     Rule<Piece<?>, Collection<Action<?>>> actionRule,
                     Rule<Piece<?>, Collection<Impact<?>>> impactRule) {

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
        var possibleMoves = actions.stream()
                .map(action -> (PieceMoveAction<?,?>) action)
                .map(PieceMoveAction::getTarget)
                .collect(toSet());

        if (!possibleMoves.contains(position)) {
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
        var possibleCaptures = possibleActions.stream()
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .map(AbstractCaptureAction::getTarget)
                .collect(toSet());

        if (!possibleCaptures.contains(targetPiece)) {
            throw new IllegalActionException(
                    String.format("%s invalid capture of %s", piece, targetPiece)
            );
        }

        ((AbstractPiece<?>) piece).doCapture(targetPiece);
    }
}