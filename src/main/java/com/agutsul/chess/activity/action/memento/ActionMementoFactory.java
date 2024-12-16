package com.agutsul.chess.activity.action.memento;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.activity.action.Action.Type;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceTypeLazyInitializer;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.Positionable;

public enum ActionMementoFactory
        implements Function<Action<?>,ActionMemento<?,?>> {

    MOVE_MODE(Action.Type.MOVE) {

        @Override
        public ActionMemento<?,?> apply(Action<?> action) {
            return createMemento((PieceMoveAction<?,?>) action);
        }

        private static ActionMemento<?,?> createMemento(PieceMoveAction<?,?> action) {
            return createMemento(
                    (Type) action.getType(),
                    action.getSource(),
                    action.getPosition()
            );
        }
    },
    CAPTURE_MODE(Action.Type.CAPTURE) {

        @Override
        public ActionMemento<?,?> apply(Action<?> action) {
            return createMemento((PieceCaptureAction<?,?,?,?>) action);
        }

        private static ActionMemento<?,?> createMemento(PieceCaptureAction<?,?,?,?> action) {
            return createMemento(
                    (Type) action.getType(),
                    action.getSource(),
                    action.getPosition()
            );
        }
    },
    PROMOTE_MODE(Action.Type.PROMOTE) {

        @Override
        public ActionMemento<?,?> apply(Action<?> action) {
            return createMemento((PiecePromoteAction<?,?>) action);
        }

        private static PromoteActionMemento createMemento(PiecePromoteAction<?,?> action) {
            var originAction = action.getSource();
            var memento = createMemento(
                    (Type) originAction.getType(),
                    originAction.getSource(),
                    ((Positionable) originAction).getPosition()
            );

            var pieceTypeInitializer = new PieceTypeLazyInitializer(action);
            return new PromoteActionMemento(
                    (Type) action.getType(),
                    pieceTypeInitializer,
                    memento
            );
        }
    },
    CASTLING_MODE(Action.Type.CASTLING) {

        @Override
        public ActionMemento<?,?> apply(Action<?> action) {
            return createMemento((PieceCastlingAction<?,?,?>) action);
        }

        private static CastlingActionMemento createMemento(PieceCastlingAction<?,?,?> action) {
            Predicate<CastlingMoveAction<?,?>> predicate =
                    moveAction -> Objects.equals(action.getPosition(), moveAction.getPosition());

            var kingAction = filter(action, predicate);
            var rookAction = filter(action, predicate.negate());

            return new CastlingActionMemento(
                    action.getCode(),
                    (Type) action.getType(),
                    createMemento(kingAction),
                    createMemento(rookAction)
            );
        }

        private static CastlingMoveAction<?,?> filter(PieceCastlingAction<?,?,?> castlingAction,
                                                      Predicate<CastlingMoveAction<?,?>> predicate) {

            var action = Stream.of(castlingAction.getSource(), castlingAction.getTarget())
                    .filter(moveAction -> predicate.test(moveAction))
                    .findFirst()
                    .get();

            return action;
        }

        private static ActionMemento<String,String> createMemento(CastlingMoveAction<?,?> action) {
            return createMemento(
                    (Type) action.getType(),
                    action.getSource(),
                    action.getPosition()
            );
        }
    },
    EN_PASSANT_MODE(Action.Type.EN_PASSANT) {

        @Override
        public ActionMemento<?,?> apply(Action<?> action) {
            return createMemento((PieceEnPassantAction<?,?,?,?>) action);
        }

        private static EnPassantActionMemento createMemento(PieceEnPassantAction<?,?,?,?> action) {
            var sourcePawn = action.getSource();
            var targetPawn = action.getTarget();

            var memento = createMemento(
                    Action.Type.CAPTURE,
                    sourcePawn,
                    targetPawn.getPosition()
            );

            return new EnPassantActionMemento(
                    (Type) action.getType(),
                    memento,
                    action.getPosition()
            );
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

    public static ActionMemento<?,?> createMemento(Action<?> action) {
        return MODES.get(action.getType()).apply(action);
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