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

public enum CancelActionMementoFactory
        implements BiFunction<Board,ActionMemento<?,?>,Action<?>> {

    MOVE_MODE(Action.Type.MOVE) {

        @Override
        @SuppressWarnings("unchecked")
        public Action<?> apply(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (ActionMemento<String,String>) memento;

            var piece = board.getPiece(actionMemento.getTarget());
            var position = board.getPosition(actionMemento.getSource());

            return create(piece.get(), position.get());
        }

        @SuppressWarnings("unchecked")
        private static <COLOR extends Color,
                        PIECE extends Piece<COLOR> & Movable>
                CancelMoveAction<COLOR,PIECE> create(Piece<Color> piece, Position position) {

            return new CancelMoveAction<>((PIECE) piece, position);
        }
    },
    CAPTURE_MODE(Action.Type.CAPTURE) {

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
                CancelCaptureAction<COLOR1,COLOR2,PIECE1,PIECE2> create(Piece<Color> predator,
                                                                        Piece<Color> victim) {

            return new CancelCaptureAction<>((PIECE1) predator, (PIECE2) victim);
        }
    },
    PROMOTE_MODE(Action.Type.PROMOTE) {

        private static final String UNSUPPORTED_ACTION_MESSAGE =
                "Unsupported promotion action";

        @Override
        public Action<?> apply(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (PromoteActionMemento) memento;
            var originMemento = actionMemento.getTarget();

            var action = createAction(board, originMemento);

            switch (originMemento.getActionType()) {
            case Action.Type.MOVE:
                return create((CancelMoveAction<?,?>) action);
            case Action.Type.CAPTURE:
                return create((CancelCaptureAction<?,?,?,?>) action);
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
    },
    CASTLING_MODE(Action.Type.CASTLING) {

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
                UncastlingMoveAction<COLOR,PIECE> create(Piece<Color> piece, Position position) {

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
    },
    EN_PASSANT_MODE(Action.Type.EN_PASSANT) {

        @Override
        public Action<?> apply(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (EnPassantActionMemento) memento;
            var captureMemento = actionMemento.getSource();

            var predator = board.getPiece(actionMemento.getTarget());
            var victim = board.getCapturedPiece(
                    captureMemento.getTarget(),
                    actionMemento.getColor().invert()
            );

            return create(predator.get(), victim.get());
        }

        @SuppressWarnings("unchecked")
        private static <COLOR1 extends Color,
                        COLOR2 extends Color,
                        PAWN1 extends PawnPiece<COLOR1>,
                        PAWN2 extends PawnPiece<COLOR2>>
                CancelEnPassantAction<COLOR1,COLOR2,PAWN1,PAWN2> create(Piece<Color> predator,
                                                                        Piece<Color> victim) {

            return new CancelEnPassantAction<>((PAWN1) predator, (PAWN2) victim);
        }
    };

    private static final Map<Action.Type, CancelActionMementoFactory> MODES =
            Stream.of(values()).collect(toMap(CancelActionMementoFactory::type, identity()));

    private Action.Type type;

    CancelActionMementoFactory(Action.Type type) {
        this.type = type;
    }

    private Action.Type type() {
        return type;
    }

    public static Action<?> createAction(Board board, ActionMemento<?,?> memento) {
        return MODES.get(memento.getActionType()).apply(board, memento);
    }
}