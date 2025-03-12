package com.agutsul.chess.activity.action.adapter;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.action.Action;

public enum ActionAdapter {
    INSTANCE;

    private String format(String source, String target) {
        return String.format("%s %s", source, target);
    }

    public static final String adapt(Positionable piece, String target) {
        return INSTANCE.format(String.valueOf(piece.getPosition()), target);
    }

    public static String adapt(Action<?> action) {
        switch (action.getType()) {
        case Action.Type.PROMOTE:
            return adapt((Action<?>) action.getSource());
        case Action.Type.CASTLING:
            return adapt((Action<?>) action.getSource());
        default:
            return adapt(
                    ((Positionable) action.getSource()), // source position
                    String.valueOf(action.getPosition()) // target position
            );
        }
    }
}