package com.agutsul.chess.command;

import static com.agutsul.chess.activity.action.Action.isCastling;
import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Objects;
import java.util.function.Predicate;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.event.ActionExecutionEvent;
import com.agutsul.chess.activity.action.event.ActionPerformedEvent;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.IllegalPositionException;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.position.Position;

public final class PerformActionCommand
        extends AbstractCommand {

    private static final Logger LOGGER = getLogger(PerformActionCommand.class);

    private static final String MISSED_PIECE_MESSAGE = "Missed piece on position";
    private static final String MISSED_POSITION_MESSAGE = "Missed position";
    private static final String OPPONENT_PIECE_MESSAGE = "Unable to use opponent piece";

    private final Player player;
    private final Board board;
    private final Observable observable;

    private Piece<?> sourcePiece;
    private Position targetPosition;

    private Action<?> action;
    private ActionMemento<?,?> memento;

    public PerformActionCommand(Player player, Board board, Observable observable) {
        super(LOGGER);
        this.player = player;
        this.board = board;
        this.observable = observable;
    }

    public void setSource(String source) {
        var foundPiece = board.getPiece(source);
        if (foundPiece.isEmpty()) {
            throw new IllegalPositionException(
                    String.format("%s: %s", MISSED_PIECE_MESSAGE, source)
            );
        }

        var piece = foundPiece.get();
        if (!Objects.equals(piece.getColor(), player.getColor())) {
            throw new IllegalActionException(
                    String.format("%s: %s", OPPONENT_PIECE_MESSAGE, piece)
            );
        }

        this.sourcePiece = piece;
    }

    public void setTarget(String target) {
        var position = board.getPosition(target);
        if (position.isEmpty()) {
            throw new IllegalPositionException(
                    String.format("%s: %s", MISSED_POSITION_MESSAGE, target)
            );
        }

        this.targetPosition = position.get();
    }

    @Override
    protected void preExecute() throws CommandException {
        var actionMatcher = new ActionMatcher(this.sourcePiece, this.targetPosition);

        var allActions = board.getActions(this.sourcePiece);
        var targetAction = allActions.stream()
                .filter(action -> actionMatcher.test(action))
                .findFirst();

        if (targetAction.isEmpty()) {
            throw new IllegalActionException(
                    String.format("Invalid action for %s at '%s' and position '%s'",
                            lowerCase(this.sourcePiece.getType().name()),
                            this.sourcePiece.getPosition(),
                            this.targetPosition
                    )
            );
        }

        this.action = targetAction.get();
    }

    @Override
    protected void executeInternal() throws CommandException {
        this.observable.notifyObservers(
                new ActionExecutionEvent(this.player, this.action)
        );

        try {
            this.memento = createMemento(this.board, this.action);
            this.action.execute();
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }

        this.observable.notifyObservers(
                new ActionPerformedEvent(this.player, this.memento)
        );
    }

    private static final class ActionMatcher
            implements Predicate<Action<?>> {

        private final Piece<?> piece;
        private final Position position;

        ActionMatcher(Piece<?> piece, Position position) {
            this.piece = piece;
            this.position = position;
        }

        @Override
        public boolean test(Action<?> action) {
            if (isCastling(action)) {
                var castlingAction = (PieceCastlingAction<?,?,?>) action;
                return test(castlingAction.getSource())
                        || test(castlingAction.getTarget());
            }

            return Objects.equals(action.getPiece(), piece)
                    && Objects.equals(action.getPosition(), position);
        }
    }
}