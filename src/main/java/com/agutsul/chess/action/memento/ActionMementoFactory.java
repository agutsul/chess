package com.agutsul.chess.action.memento;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceCastlingAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;

public enum ActionMementoFactory {
    INSTANCE;

    private final Map<Action.Type, Function<Action<?>, ActionMemento>> MODES =
                new EnumMap<>(Action.Type.class);

    private ActionMementoFactory() {
        MODES.put(Action.Type.MOVE,       action -> create((PieceMoveAction<?,?>)          action));
        MODES.put(Action.Type.CAPTURE,    action -> create((PieceCaptureAction<?,?,?,?>)   action));
        MODES.put(Action.Type.PROMOTE,    action -> create((PiecePromoteAction<?,?>)       action));
        MODES.put(Action.Type.CASTLING,   action -> create((PieceCastlingAction<?,?,?>)    action));
        MODES.put(Action.Type.EN_PASSANT, action -> create((PieceEnPassantAction<?,?,?,?>) action));
    }

    public ActionMemento create(Action<?> action) {
        return MODES.get(action.getType()).apply(action);
    }

    private ActionMemento create(PieceCaptureAction<?, ?, ?, ?> action) {
        var source = action.getSource();
        var target = action.getTarget();

        return new ActionMemento(source.getPosition(), target.getPosition());
    }

    private ActionMemento create(PieceMoveAction<?, ?> action) {
        return new ActionMemento(action.getSource().getPosition(), action.getTarget());
    }

    private ActionMemento create(PieceCastlingAction<?, ?, ?> action) {
        var subAction = action.kingCastlingAction();
        return new ActionMemento(subAction.getSource().getPosition(), action.getPosition());
    }

    private ActionMemento create(PieceEnPassantAction<?, ?, ?, ?> action) {
        return new ActionMemento(action.getSource().getPosition(), action.getPosition());
    }

    private ActionMemento create(PiecePromoteAction<?, ?> action) {
        var originAction = action.getSource();
        return new ActionMemento(originAction.getSource().getPosition(), originAction.getPosition());
    }
}