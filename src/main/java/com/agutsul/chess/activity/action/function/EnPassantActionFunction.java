package com.agutsul.chess.activity.action.function;

import static com.agutsul.chess.activity.action.Action.isEnPassant;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceEnPassantAction;

final class EnPassantActionFunction
        implements Function<Action<?>,Optional<PieceEnPassantAction<?,?,?,?>>> {

    @Override
    public Optional<PieceEnPassantAction<?,?,?,?>> apply(Action<?> action) {
        return Optional.ofNullable(isEnPassant(action)
                ? (PieceEnPassantAction<?,?,?,?>) action
                : null
        );
    }
}