package com.agutsul.chess.activity.action.function;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;

final class CastlingActionFunction
        implements Function<Action<?>, Optional<PieceCastlingAction<?,?,?>>> {

    @Override
    public Optional<PieceCastlingAction<?,?,?>> apply(Action<?> action) {
        if (Action.Type.CASTLING.equals(action.getType())) {
            return Optional.of((PieceCastlingAction<?,?,?>) action);
        }

        return Optional.empty();
    }
}