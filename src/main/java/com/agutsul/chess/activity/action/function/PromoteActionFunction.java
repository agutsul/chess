package com.agutsul.chess.activity.action.function;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PiecePromoteAction;

final class PromoteActionFunction
        implements Function<Action<?>, Optional<PiecePromoteAction<?,?>>> {

    @Override
    public Optional<PiecePromoteAction<?,?>> apply(Action<?> action) {
        if (Action.Type.PROMOTE.equals(action.getType())) {
            return Optional.of((PiecePromoteAction<?,?>) action);
        }

        return Optional.empty();
    }
}