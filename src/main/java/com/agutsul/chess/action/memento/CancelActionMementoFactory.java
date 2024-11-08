package com.agutsul.chess.action.memento;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.CancelCaptureAction;
import com.agutsul.chess.action.CancelCastlingAction;
import com.agutsul.chess.action.CancelCastlingAction.UncastlingMoveAction;
import com.agutsul.chess.action.CancelEnPassantAction;
import com.agutsul.chess.action.CancelMoveAction;
import com.agutsul.chess.action.CancelPromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.PawnPiece;

public enum CancelActionMementoFactory {
    MOVE_MODE(Action.Type.MOVE) {

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Action<?> create(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (ActionMemento<String,String>) memento;

            var piece = board.getPiece(actionMemento.getTarget());
            var position = board.getPosition(actionMemento.getSource());

            return new CancelMoveAction(piece.get(), position.get());
        }
    },
    CAPTURE_MODE(Action.Type.CAPTURE) {

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Action<?> create(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (ActionMemento<String,String>) memento;

            var predator = board.getPiece(actionMemento.getTarget());
            var victim = board.getCapturedPiece(
                    actionMemento.getTarget(),
                    actionMemento.getColor().invert()
            );

            return new CancelCaptureAction(predator.get(), victim.get());
        }
    },
    PROMOTE_MODE(Action.Type.PROMOTE) {

        private static final String UNSUPPORTED_ACTION_MESSAGE =
                "Unsupported promotion action";

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Action<?> create(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (PromoteActionMemento) memento;
            var originMemento = actionMemento.getTarget();

            if (Action.Type.MOVE.equals(originMemento.getActionType())) {
                var originAction = MOVE_MODE.create(board, originMemento);
                return new CancelPromoteAction((CancelMoveAction<?,?>) originAction);
            }

            if (Action.Type.CAPTURE.equals(originMemento.getActionType())) {
                var originAction = CAPTURE_MODE.create(board, originMemento);
                return new CancelPromoteAction((CancelCaptureAction<?,?,?,?>) originAction);
            }

            throw new IllegalActionException(String.format("%s: %s",
                    UNSUPPORTED_ACTION_MESSAGE,
                    originMemento.getActionType()
            ));
        }
    },
    CASTLING_MODE(Action.Type.CASTLING) {

        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Action<?> create(Board board, ActionMemento<?,?> memento) {
            var castlingMemento = (CastlingActionMemento) memento;

            var kingAction = uncastlingAction(board, castlingMemento.getSource());
            var rookAction = uncastlingAction(board, castlingMemento.getTarget());

            return new CancelCastlingAction(castlingMemento.getCode(), kingAction, rookAction);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private UncastlingMoveAction<?,?> uncastlingAction(Board board,
                                                           ActionMemento<String,String> memento) {
            var piece = board.getPiece(memento.getTarget());
            var position = board.getPosition(memento.getSource());

            return new UncastlingMoveAction(piece.get(), position.get());
        }
    },
    EN_PASSANT_MODE(Action.Type.EN_PASSANT) {

        @Override
        Action<?> create(Board board, ActionMemento<?,?> memento) {
            var actionMemento = (EnPassantActionMemento) memento;
            var captureMemento = actionMemento.getSource();

            var predator = board.getPiece(actionMemento.getTarget());
            var victim = board.getCapturedPiece(
                    captureMemento.getTarget(),
                    actionMemento.getColor().invert()
            );

            return new CancelEnPassantAction<>(
                    (PawnPiece<?>) predator.get(),
                    (PawnPiece<?>) victim.get()
            );
        }
    };

    private static final Map<Action.Type, CancelActionMementoFactory> MODES =
            Stream.of(values()).collect(toMap(CancelActionMementoFactory::type, identity()));

    private Action.Type type;

    CancelActionMementoFactory(Action.Type type) {
        this.type = type;
    }

    Action.Type type() {
        return type;
    }

    abstract Action<?> create(Board board, ActionMemento<?,?> memento);

    public static Action<?> createAction(Board board, ActionMemento<?,?> memento) {
        return MODES.get(memento.getActionType()).create(board, memento);
    }
}