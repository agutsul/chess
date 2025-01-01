package com.agutsul.chess.activity.action.memento;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.Action.Type;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceCastlingAction.CastlingMoveAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.PieceTypeLazyInitializer;
import com.agutsul.chess.position.Position;

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

    private static ActionMemento<String, String> createMemento(Action.Type actionType,
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

    private static String createCode(Piece<?> piece, Position position) {
        var sourcePosition = piece.getPosition();
        var code = Position.codeOf(sourcePosition);

        var label = sourcePosition.x() == position.x()
                ? code.charAt(1)  // y
                : code.charAt(0); // x

        return String.valueOf(label);
    }

    public static ActionMemento<?,?> createMemento(Board board, Action<?> action) {
        var memento = MODES.get(action.getType()).apply(action);
        if (Action.Type.CASTLING.equals(action.getType())) {
            return memento;
        }

        // There are cases when multiple pieces of the same color and type can perform an action.
        // To be able to clearly identify source piece while reviewing journal additional code should be provided.
        // Code can be either the first position symbol or the last one ( if the first matches )

        var source = Action.Type.PROMOTE.equals(action.getType())
                ? ((Action<?>) action.getSource()).getSource()
                : action.getSource();

        var sourcePiece = (Piece<?>) source;

        var allPieces = board.getPieces(sourcePiece.getColor(), sourcePiece.getType());
        if (allPieces.size() == 1) {
            return memento;
        }

        var targetPosition = action.getPosition();
        var code = allPieces.stream()
                .filter(piece -> !Objects.equals(piece, sourcePiece))
                .filter(piece -> {
                    var actions = board.getActions(piece);
                    var isFound = actions.stream()
                            .filter(a -> !Action.Type.CASTLING.equals(a.getType()))
                            .map(Action::getPosition)
                            .anyMatch(position -> Objects.equals(position, targetPosition));

                    return isFound;
                })
                .map(piece -> createCode(sourcePiece, piece.getPosition()))
                .findFirst()
                .orElse(null);

        return new ActionMementoDecorator<>(memento, code);
    }
}