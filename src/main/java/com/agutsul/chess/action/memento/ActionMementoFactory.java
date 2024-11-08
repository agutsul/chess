package com.agutsul.chess.action.memento;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public enum ActionMementoFactory {
    MOVE_MODE(Action.Type.MOVE) {

        @Override
        ActionMemento<?,?> create(Action<?> action) {
            return createMemento((PieceMoveAction<?,?>) action);
        }

        private static ActionMemento<?,?> createMemento(PieceMoveAction<?,?> action) {
            return createMemento(
                    action.getType(),
                    action.getSource(),
                    action.getTarget()
            );
        }
    },
    CAPTURE_MODE(Action.Type.CAPTURE) {

        @Override
        ActionMemento<?,?> create(Action<?> action) {
            return createMemento((PieceCaptureAction<?,?,?,?>) action);
        }

        private static ActionMemento<?,?> createMemento(PieceCaptureAction<?,?,?,?> action) {
            return createMemento(
                    action.getType(),
                    action.getSource(),
                    action.getTarget().getPosition()
            );
        }
    },
    PROMOTE_MODE(Action.Type.PROMOTE) {

        @Override
        ActionMemento<?,?> create(Action<?> action) {
            return createMemento((PiecePromoteAction<?,?>) action);
        }

        private static PromoteActionMemento createMemento(PiecePromoteAction<?,?> action) {
            var originAction = action.getSource();
            var memento = createMemento(
                    originAction.getType(),
                    originAction.getSource(),
                    originAction.getPosition()
            );

            return new PromoteActionMemento(action.getType(), action.getPieceType(), memento);
        }
    },
    CASTLING_MODE(Action.Type.CASTLING) {

        @Override
        ActionMemento<?,?> create(Action<?> action) {
            return createMemento((PieceCastlingAction<?,?,?>) action);
        }

        private static CastlingActionMemento createMemento(PieceCastlingAction<?,?,?> action) {
            var kingPosition = action.getPosition();

            var kingAction = filterAction(action,
                    moveAction -> Objects.equals(kingPosition,  moveAction.getPosition()));

            var rookAction = filterAction(action,
                    moveAction -> !Objects.equals(kingPosition, moveAction.getPosition()));

            return new CastlingActionMemento(
                    action.getCode(),
                    action.getType(),
                    createMemento(kingAction),
                    createMemento(rookAction)
            );
        }

        private static CastlingMoveAction<?,?> filterAction(PieceCastlingAction<?,?,?> castlingAction,
                                                            Predicate<CastlingMoveAction<?,?>> predicate) {

            var action = Stream.of(castlingAction.getSource(), castlingAction.getTarget())
                    .filter(moveAction -> predicate.test(moveAction))
                    .findFirst()
                    .get();

            return action;
        }

        private static ActionMemento<String,String> createMemento(CastlingMoveAction<?,?> action) {
            return createMemento(
                    action.getType(),
                    action.getSource(),
                    action.getPosition()
            );
        }
    },
    EN_PASSANT_MODE(Action.Type.EN_PASSANT) {

        @Override
        ActionMemento<?,?> create(Action<?> action) {
            return createMemento((PieceEnPassantAction<?,?,?,?>) action);
        }

        private static EnPassantActionMemento createMemento(PieceEnPassantAction<?,?,?,?> action) {
            var sourcePawn = action.getSource();
            var targetPawn = action.getTarget();

            var memento = createMemento(Action.Type.CAPTURE, sourcePawn, targetPawn.getPosition());
            return new EnPassantActionMemento(action.getType(), memento, action.getPosition());
        }
    };

    private static final Map<Action.Type, ActionMementoFactory> MODES =
                Stream.of(values()).collect(toMap(ActionMementoFactory::type, identity()));

    private Action.Type type;

    ActionMementoFactory(Action.Type type) {
        this.type = type;
    }

    private Action.Type type() {
        return type;
    }

    abstract ActionMemento<?,?> create(Action<?> action);

    public static ActionMemento<?,?> createMemento(Action<?> action) {
        return MODES.get(action.getType()).create(action);
    }

    static ActionMemento<String,String> createMemento(Action.Type actionType,
                                                      Piece<?> sourcePiece,
                                                      Position targetPosition) {
        return new ActionMementoImpl<>(
                sourcePiece.getColor(),
                actionType,
                sourcePiece.getType(),
                String.valueOf(sourcePiece.getPosition()),
                String.valueOf(targetPosition)
        );
    }
}