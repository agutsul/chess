package com.agutsul.chess.command;

import static com.agutsul.chess.activity.action.memento.ActionMementoFactory.createMemento;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
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
        var actionFilter = new ActionFilter(this.sourcePiece, this.targetPosition);

        var allActions = board.getActions(this.sourcePiece);
        var targetAction = allActions.stream()
                .filter(action -> actionFilter.test(action))
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

    private static final class ActionFilter
            implements Predicate<Action<?>> {

        private final Piece<?> piece;
        private final Position position;

        ActionFilter(Piece<?> piece, Position position) {
            this.piece = piece;
            this.position = position;
        }

        @Override
        public boolean test(Action<?> action) {
            return ActionMatcher.matches(action, this.piece, this.position);
        }

        private enum ActionMatcher {
            MOVE_MODE(Action.Type.MOVE) {
                @Override
                boolean equals(Action<?> action, Piece<?> piece, Position position) {
                    var moveAction = (PieceMoveAction<?,?>) action;
                    return Objects.equals(moveAction.getSource(), piece)
                            && Objects.equals(moveAction.getPosition(), position);
                }
            },
            CAPTURE_MODE(Action.Type.CAPTURE) {
                @Override
                boolean equals(Action<?> action, Piece<?> piece, Position position) {
                    var captureAction = (PieceCaptureAction<?,?,?,?>) action;
                    return Objects.equals(captureAction.getSource(), piece)
                            && Objects.equals(captureAction.getPosition(), position);
                }
            },
            PROMOTE_MODE(Action.Type.PROMOTE) {
                @Override
                boolean equals(Action<?> action, Piece<?> piece, Position position) {
                    var promoteAction = (PiecePromoteAction<?,?>) action;
                    var originAction = (Action<?>) promoteAction.getSource();

                    return matches(originAction, piece, position);
                }
            },
            CASTLING_MODE(Action.Type.CASTLING) {
                @Override
                boolean equals(Action<?> action, Piece<?> piece, Position position) {
                    var castlingAction = (PieceCastlingAction<?,?,?>) action;
                    if (matches(castlingAction.getSource(), piece, position)) {
                        return true;
                    }

                    return matches(castlingAction.getTarget(), piece, position);
                }
            },
            EN_PASSANT_MODE(Action.Type.EN_PASSANT) {
                @Override
                boolean equals(Action<?> action, Piece<?> piece, Position position) {
                    var enPassantAction = (PieceEnPassantAction<?,?,?,?>) action;
                    return Objects.equals(enPassantAction.getSource(), piece)
                            && Objects.equals(enPassantAction.getPosition(), position);
                }
            };

            private static final Map<Action.Type, ActionMatcher> MODES =
                    Stream.of(values()).collect(toMap(ActionMatcher::type, identity()));

            private Action.Type type;

            ActionMatcher(Action.Type type) {
                this.type = type;
            }

            abstract boolean equals(Action<?> action, Piece<?> piece, Position position);

            private Action.Type type() {
                return type;
            }

            private static boolean matches(Action<?> action, Piece<?> source, Position target) {
                return MODES.get(action.getType()).equals(action, source, target);
            }
        }
    }
}