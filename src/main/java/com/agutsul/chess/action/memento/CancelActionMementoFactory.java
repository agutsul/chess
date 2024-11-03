package com.agutsul.chess.action.memento;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

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
    INSTANCE;

    private final Map<Action.Type, BiFunction<Board, ActionMemento<?,?>, Action<?>>> MODES =
                new EnumMap<>(Action.Type.class);

    CancelActionMementoFactory() {
        MODES.put(Action.Type.MOVE,       (board, memento) -> cancelMoveAction(board, memento));
        MODES.put(Action.Type.CAPTURE,    (board, memento) -> cancelCaptureAction(board, memento));
        MODES.put(Action.Type.PROMOTE,    (board, memento) -> cancelPromoteAction(board, memento));
        MODES.put(Action.Type.CASTLING,   (board, memento) -> cancelCastlingAction(board, memento));
        MODES.put(Action.Type.EN_PASSANT, (board, memento) -> cancelEnPassantAction(board, memento));
    }

    public Action<?> create(Board board, ActionMemento<?,?> memento) {
        return MODES.get(memento.getActionType()).apply(board, memento);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Action<?> cancelMoveAction(Board board, ActionMemento<?,?> memento) {
        var actionMemento = (ActionMemento<String,String>) memento;

        var piece = board.getPiece(actionMemento.getTarget());
        var position = board.getPosition(actionMemento.getSource());

        return new CancelMoveAction(piece.get(), position.get());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Action<?> cancelCaptureAction(Board board, ActionMemento<?,?> memento) {
        var actionMemento = (ActionMemento<String,String>) memento;

        var predator = board.getPiece(actionMemento.getTarget());
        var victim = board.getCapturedPiece(actionMemento.getTarget());

        return new CancelCaptureAction(predator.get(), victim.get());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Action<?> cancelPromoteAction(Board board, ActionMemento<?,?> memento) {
        var actionMemento = (ActionMemento<String,ActionMemento<String,String>>) memento;
        var originMemento = actionMemento.getTarget();

        if (Action.Type.MOVE.equals(originMemento.getActionType())) {
            var originAction = cancelMoveAction(board, originMemento);
            return new CancelPromoteAction((CancelMoveAction<?,?>) originAction);
        }

        if (Action.Type.CAPTURE.equals(originMemento.getActionType())) {
            var originAction = cancelCaptureAction(board, originMemento);
            return new CancelPromoteAction((CancelCaptureAction<?,?,?,?>) originAction);
        }

        throw new IllegalActionException(String.format(
                "Unsupported promotion action: %s",
                originMemento.getActionType()
        ));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Action<?> cancelCastlingAction(Board board, ActionMemento<?,?> memento) {
        var castlingMemento = (CastlingActionMemento) memento;

        var kingMemento = castlingMemento.getSource();
        var kingPiece = board.getPiece(kingMemento.getTarget());
        var kingTargetPosition = board.getPosition(kingMemento.getSource());

        var rookMemento = castlingMemento.getTarget();
        var rookPiece = board.getPiece(rookMemento.getTarget());
        var rookTargetPosition = board.getPosition(rookMemento.getSource());

        return new CancelCastlingAction(
                castlingMemento.getCode(),
                new UncastlingMoveAction(kingPiece.get(), kingTargetPosition.get()),
                new UncastlingMoveAction(rookPiece.get(), rookTargetPosition.get())
        );
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static Action<?> cancelEnPassantAction(Board board, ActionMemento<?,?> memento) {
        var actionMemento = (ActionMemento<String,ActionMemento<String,String>>) memento;
        var enPassantMemento = actionMemento.getTarget();

        var predator = board.getPiece(enPassantMemento.getTarget()).get();
        var victim = board.getCapturedPiece(enPassantMemento.getSource()).get();

        return new CancelEnPassantAction((PawnPiece<?>) predator, (PawnPiece<?>) victim);
    }
}