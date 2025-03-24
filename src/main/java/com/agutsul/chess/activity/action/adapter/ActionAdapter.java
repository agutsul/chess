package com.agutsul.chess.activity.action.adapter;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public interface ActionAdapter {

    default String adapt(Action<?> action) {
        return adapt(action.getPiece(), action.getPosition());
    }

    default String adapt(Piece<?> piece, Position target) {
        return adapt(piece, String.valueOf(target));
    }

    default String adapt(Piece<?> piece, String target) {
        return format(String.valueOf(piece.getPosition()), target);
    }

    private String format(String sourcePosition, String targetPosition) {
        return String.format("%s %s", sourcePosition, targetPosition);
    }
}