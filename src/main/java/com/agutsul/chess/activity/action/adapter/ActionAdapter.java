package com.agutsul.chess.activity.action.adapter;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.piece.Piece;

public enum ActionAdapter {
    INSTANCE;

    private String format(String source, String target) {
        return String.format("%s %s", source, target);
    }

    public static final String adapt(Piece<?> piece, String target) {
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
                    ((Piece<?>) action.getSource()),     // source piece
                    String.valueOf(action.getPosition()) // target position
            );
        }
    }
}