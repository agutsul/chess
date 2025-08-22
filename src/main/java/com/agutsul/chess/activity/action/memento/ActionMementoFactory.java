package com.agutsul.chess.activity.action.memento;

import static com.agutsul.chess.activity.action.Action.isCastling;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.concurrent.LazyInitializer;

import com.agutsul.chess.activity.action.AbstractCastlingAction;
import com.agutsul.chess.activity.action.AbstractMoveAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class ActionMementoFactory {

    public static ActionMemento<?,?> createMemento(Board board, Action<?> action) {
        ActionMemento<?,?> memento = FactoryMode.MODES.get(action.getType()).apply(action);
        if (isCastling(action)) {
            return memento;
        }

        // There are cases when multiple pieces of the same color and type can perform an action.
        // To be able to clearly identify source piece while reviewing journal additional code should be provided.
        // Code can be either the first position symbol or the last one ( if the first matches )

        var sourcePiece = action.getPiece();

        var similarPieces = board.getPieces(sourcePiece.getColor(), sourcePiece.getType());
        if (similarPieces.size() == 1) {
            return memento;
        }

        var targetPosition = action.getPosition();
        Optional<ActionMemento<?,?>> decoratedMemento = similarPieces.stream()
                .filter(piece -> !Objects.equals(piece, sourcePiece))
                .filter(piece -> isPositionAccessible(board.getActions(piece), targetPosition))
                .map(piece -> createCode(sourcePiece, piece.getPosition()))
                .map(code -> new ActionMementoDecorator<>(memento, code))
                .findFirst()
                .map(actionMemento -> (ActionMemento<?,?>) actionMemento);

        return decoratedMemento.orElse(memento);
    }

    private static String createCode(Piece<?> piece, Position position) {
        var sourcePosition = piece.getPosition();
        var label = String.valueOf(sourcePosition);

        var code = sourcePosition.x() == position.x()
                ? label.charAt(1)  // y
                : label.charAt(0); // x

        return String.valueOf(code);
    }

    private static boolean isPositionAccessible(Collection<Action<?>> actions,
                                                Position targetPosition) {

        var isAccessible = Stream.of(actions)
                .flatMap(Collection::stream)
                .filter(not(Action::isCastling))
                .map(Action::getPosition)
                .anyMatch(position -> Objects.equals(position, targetPosition));

        return isAccessible;
    }

    private enum FactoryMode implements Function<Action<?>,ActionMemento<?,?>> {
        MOVE_MODE(Action.Type.MOVE),
        BIG_MOVE_MODE(Action.Type.BIG_MOVE),
        CAPTURE_MODE(Action.Type.CAPTURE),
        PROMOTE_MODE(Action.Type.PROMOTE) {

            @Override
            public ActionMemento<?,?> apply(Action<?> action) {
                var promoteAction = (PiecePromoteAction<?,?>) action;
                var originAction  = (Action<?>) promoteAction.getSource();

                @SuppressWarnings("unchecked")
                var memento = (ActionMemento<String,String>) super.apply(originAction);
                return new PromoteActionMemento(
                        promoteAction.getType(),
                        new PieceTypeLazyInitializer(promoteAction),
                        memento
                );
            }

            static final class PieceTypeLazyInitializer
                    extends LazyInitializer<Piece.Type> {

                private final PiecePromoteAction<?,?> action;

                PieceTypeLazyInitializer(PiecePromoteAction<?,?> action) {
                    this.action = action;
                }

                @Override
                protected Piece.Type initialize() {
                    return action.getPieceType();
                }
            }
        },
        CASTLING_MODE(Action.Type.CASTLING) {

            @Override
            public ActionMemento<?,?> apply(Action<?> action) {
                var castlingAction = (AbstractCastlingAction<?,?,?,?,?>) action;

                return new CastlingActionMemento(
                        castlingAction.getSide(),
                        castlingAction.getType(),
                        createMemento(castlingAction.getSource()),
                        createMemento(castlingAction.getTarget())
                );
            }

            @SuppressWarnings("unchecked")
            private ActionMemento<String,String> createMemento(AbstractMoveAction<?,?> action) {
                return (ActionMemento<String,String>) super.apply(action);
            }
        },
        EN_PASSANT_MODE(Action.Type.EN_PASSANT) {

            @Override
            public ActionMemento<?,?> apply(Action<?> action) {
                var enPassantAction = (PieceEnPassantAction<?,?,?,?>) action;

                var sourcePawn = enPassantAction.getSource();
                var targetPawn = enPassantAction.getTarget();

                var memento = createMemento(
                        Action.Type.CAPTURE,
                        sourcePawn,
                        targetPawn.getPosition()
                );

                return new EnPassantActionMemento(
                        enPassantAction.getType(),
                        memento,
                        enPassantAction.getPosition()
                );
            }
        };

        private static final Map<Action.Type,FactoryMode> MODES =
                    Stream.of(values()).collect(toMap(FactoryMode::type, identity()));

        private Action.Type type;

        FactoryMode(Action.Type type) {
            this.type = type;
        }

        private Action.Type type() {
            return type;
        }

        @Override
        public ActionMemento<?,?> apply(Action<?> action) {
            return createMemento(action.getType(), action.getPiece(), action.getPosition());
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
}