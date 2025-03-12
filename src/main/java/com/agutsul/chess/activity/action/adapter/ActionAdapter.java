package com.agutsul.chess.activity.action.adapter;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface ActionAdapter {

    public default String adapt(Action<?> action) {
        switch (action.getType()) {
        case Action.Type.PROMOTE:
            return adapt((Action<?>) action.getSource());
        case Action.Type.CASTLING:
            return adapt((Action<?>) action.getSource());
        default:
            return adapt(
                    ((Piece<?>) action.getSource()),     // source piece
                    action.getPosition()                 // target position
            );
        }
    }

    public default String adapt(Piece<?> piece, Position target) {
        return adapt(piece, String.valueOf(target));
    }

    public default String adapt(Piece<?> piece, String target) {
        return format(String.valueOf(piece.getPosition()), target);
    }

    private String format(String sourcePosition, String targetPosition) {
        return String.format("%s %s", sourcePosition, targetPosition);
    }
}