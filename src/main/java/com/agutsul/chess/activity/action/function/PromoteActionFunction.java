package com.agutsul.chess.activity.action.function;

import static com.agutsul.chess.activity.action.Action.isPromote;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PiecePromoteAction;

final class PromoteActionFunction
        implements Function<Action<?>,Optional<PiecePromoteAction<?,?>>> {

    @Override
    public Optional<PiecePromoteAction<?,?>> apply(Action<?> action) {
        return Optional.ofNullable(isPromote(action)
                ? (PiecePromoteAction<?,?>) action
                : null
        );
    }
}