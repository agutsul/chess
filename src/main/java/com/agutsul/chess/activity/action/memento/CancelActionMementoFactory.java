package com.agutsul.chess.activity.action.memento;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Demotable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.CancelBigMoveAction;
import com.agutsul.chess.activity.action.CancelCaptureAction;
import com.agutsul.chess.activity.action.CancelCastlingAction;
import com.agutsul.chess.activity.action.CancelCastlingAction.UncastlingMoveAction;
import com.agutsul.chess.activity.action.CancelEnPassantAction;
import com.agutsul.chess.activity.action.CancelMoveAction;
import com.agutsul.chess.activity.action.CancelPromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public enum CancelActionMementoFactory {
    INSTANCE;

    public static Action<?> createAction(Board board, ActionMemento<?,?> memento) {
        return FactoryMode.MODES.get(memento.getActionType()).apply(board, memento);
    }

    private enum FactoryMode implements BiFunction<Board,ActionMemento<?,?>,Action<?>> {

        MOVE_MODE(Action.Type.MOVE,             new CancelMoveActionFunction()),
        BIG_MOVE_MODE(Action.Type.BIG_MOVE,     new CancelBigMoveActionFunction()),
        CAPTURE_MODE(Action.Type.CAPTURE,       new CancelCaptureActionFunction()),
        PROMOTE_MODE(Action.Type.PROMOTE,       new CancelPromoteActionFunction()),
        CASTLING_MODE(Action.Type.CASTLING,     new CancelCastlingActionFunction()),
        EN_PASSANT_MODE(Action.Type.EN_PASSANT, new CancelEnPassantActionFunction());

        private static final Map<Action.Type,FactoryMode> MODES =
                Stream.of(values()).collect(toMap(FactoryMode::type, identity()));

        private Action.Type type;
        private BiFunction<Board,ActionMemento<?,?>,Action<?>> function;

        FactoryMode(Action.Type type, BiFunction<Board,ActionMemento<?,?>,Action<?>> function) {
            this.type = type;
            this.function = function;
        }

        @Override
        public Action<?> apply(Board board, ActionMemento<?,?> memento) {
            return function.apply(board, memento);
        }

        private Action.Type type() {
            return type;
        }
    }

    private static abstract class AbstractCancelMoveActionFunction
            implements BiFunction<Board,ActionMemento<?,?>,Action<?>> {

        @Override
        @SuppressWarnings("unchecked")
        public final Action<?> apply(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (ActionMemento<String,String>) memento;

            var piece = board.getPiece(actionMemento.getTarget());
            var position = board.getPosition(actionMemento.getSource());

            return create(piece.get(), position.get());
        }

        abstract <COLOR extends Color,PIECE extends Piece<COLOR> & Movable>
                Action<?> create(Piece<COLOR> piece, Position position);
    }

    private static final class CancelMoveActionFunction
            extends AbstractCancelMoveActionFunction {

        @Override
        @SuppressWarnings("unchecked")
        <COLOR extends Color,PIECE extends Piece<COLOR> & Movable>
                CancelMoveAction<COLOR,PIECE> create(Piece<COLOR> piece, Position position) {

            return new CancelMoveAction<>((PIECE) piece, position);
        }
    }

    private static final class CancelBigMoveActionFunction
            extends AbstractCancelMoveActionFunction {

        @Override
        @SuppressWarnings("unchecked")
        <COLOR extends Color,PIECE extends Piece<COLOR> & Movable>
                CancelBigMoveAction<COLOR,PIECE> create(Piece<COLOR> piece, Position position) {

            return new CancelBigMoveAction<>((PIECE) piece, position);
        }
    }

    private static final class CancelCaptureActionFunction
            implements BiFunction<Board,ActionMemento<?,?>,Action<?>> {

        @Override
        @SuppressWarnings("unchecked")
        public Action<?> apply(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (ActionMemento<String,String>) memento;

            var predator = board.getPiece(actionMemento.getTarget());
            var victim = board.getCapturedPiece(
                    actionMemento.getTarget(),
                    actionMemento.getColor().invert()
            );

            return create(predator.get(), victim.get());
        }

        @SuppressWarnings("unchecked")
        private static <COLOR1 extends Color,
                        COLOR2 extends Color,
                        PIECE1 extends Piece<COLOR1> & Capturable,
                        PIECE2 extends Piece<COLOR2>>
                CancelCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> create(Piece<COLOR1> predator,
                                                                        Piece<COLOR2> victim) {

            return new CancelCaptureAction<>((PIECE1) predator, (PIECE2) victim);
        }
    }

    private static final class CancelPromoteActionFunction
            implements BiFunction<Board,ActionMemento<?,?>,Action<?>> {

        private static final String UNSUPPORTED_ACTION_MESSAGE =
                "Unsupported promotion action";

        @Override
        public Action<?> apply(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (PromoteActionMemento) memento;
            var originMemento = actionMemento.getTarget();

            var originAction = createAction(board, originMemento);

            switch (originMemento.getActionType()) {
            case Action.Type.MOVE:
                return create((CancelMoveAction<?,?>) originAction);
            case Action.Type.CAPTURE:
                return create((CancelCaptureAction<?,?,?,?>) originAction);
            default:
                throw new IllegalActionException(String.format("%s: %s",
                        UNSUPPORTED_ACTION_MESSAGE,
                        originMemento.getActionType()
                ));
            }
        }

        @SuppressWarnings("unchecked")
        private static <COLOR extends Color,
                        PIECE extends Piece<COLOR> & Movable & Capturable & Demotable>
                CancelPromoteAction<COLOR,PIECE> create(CancelMoveAction<?,?> action) {

            return new CancelPromoteAction<>((CancelMoveAction<COLOR,PIECE>) action);
        }

        @SuppressWarnings("unchecked")
        private static <COLOR1 extends Color,
                        COLOR2 extends Color,
                        PIECE1 extends Piece<COLOR1> & Movable & Capturable & Demotable,
                        PIECE2 extends Piece<COLOR2>>
                CancelPromoteAction<COLOR1,PIECE1> create(CancelCaptureAction<?,?,?,?> action) {

            return new CancelPromoteAction<>(
                    (CancelCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2>) action
            );
        }
    }

    private static final class CancelCastlingActionFunction
            implements BiFunction<Board,ActionMemento<?,?>,Action<?>> {

        @Override
        public Action<?> apply(Board board, ActionMemento<?,?> memento) {
            var castlingMemento = (CastlingActionMemento) memento;

            var kingAction = uncastlingAction(board, castlingMemento.getSource());
            var rookAction = uncastlingAction(board, castlingMemento.getTarget());

            return create(castlingMemento.getSide(), kingAction, rookAction);
        }

        private static UncastlingMoveAction<?,?> uncastlingAction(Board board,
                                                                  ActionMemento<String,String> memento) {
            var piece = board.getPiece(memento.getTarget());
            var position = board.getPosition(memento.getSource());

            return create(piece.get(), position.get());
        }

        @SuppressWarnings("unchecked")
        private static <COLOR extends Color,
                        PIECE extends Piece<COLOR> & Castlingable & Movable>
                UncastlingMoveAction<COLOR,PIECE> create(Piece<COLOR> piece, Position position) {

            return new UncastlingMoveAction<>((PIECE) piece, position);
        }

        @SuppressWarnings("unchecked")
        private static <COLOR extends Color,
                        PIECE1 extends Piece<COLOR> & Castlingable & Movable,
                        PIECE2 extends Piece<COLOR> & Castlingable & Movable>
                CancelCastlingAction<COLOR,PIECE1,PIECE2> create(Castlingable.Side side,
                                                                 UncastlingMoveAction<?,?> sourceAction,
                                                                 UncastlingMoveAction<?,?> targetAction) {

            return new CancelCastlingAction<>(
                    side,
                    (UncastlingMoveAction<COLOR,PIECE1>) sourceAction,
                    (UncastlingMoveAction<COLOR,PIECE2>) targetAction
            );
        }
    }

    private static final class CancelEnPassantActionFunction
            implements BiFunction<Board,ActionMemento<?,?>,Action<?>> {

        @Override
        public Action<?> apply(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (EnPassantActionMemento) memento;
            var captureMemento = actionMemento.getSource();

            var victim = board.getCapturedPiece(
                    captureMemento.getTarget(),
                    actionMemento.getColor().invert()
            );

            var predator = board.getPiece(actionMemento.getTarget());
            return create(predator.get(), victim.get());
        }

        @SuppressWarnings("unchecked")
        private static <COLOR1 extends Color,
                        COLOR2 extends Color,
                        PAWN1 extends PawnPiece<COLOR1>,
                        PAWN2 extends PawnPiece<COLOR2>>
                CancelEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2> create(Piece<COLOR1> predator,
                                                                        Piece<COLOR2> victim) {

            return new CancelEnPassantAction<>((PAWN1) predator, (PAWN2) victim);
        }
    }
}