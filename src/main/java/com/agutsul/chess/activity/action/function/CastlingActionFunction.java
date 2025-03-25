package com.agutsul.chess.activity.action.function;

import static com.agutsul.chess.activity.action.Action.isCastling;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;

final class CastlingActionFunction
        implements Function<Action<?>,Optional<PieceCastlingAction<?,?,?>>> {

    @Override
    public Optional<PieceCastlingAction<?,?,?>> apply(Action<?> action) {
        return Optional.ofNullable(isCastling(action)
                ? (PieceCastlingAction<?,?,?>) action
                : null
        );
    }
}