package com.agutsul.chess.activity.action.function;

import static com.agutsul.chess.activity.action.Action.isBigMove;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceBigMoveAction;
import com.agutsul.chess.activity.action.PieceMoveAction;

final class BigMoveActionFunction
        implements Function<Action<?>,Optional<PieceMoveAction<?,?>>> {

    @Override
    public Optional<PieceMoveAction<?,?>> apply(Action<?> action) {
        return Optional.ofNullable(isBigMove(action)
                ? (PieceBigMoveAction<?,?>) action
                : null
        );
    }
}