package com.agutsul.chess.action.function;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceEnPassantAction;

public final class EnPassantActionFunction
        implements Function<Action<?>, Optional<PieceEnPassantAction<?,?,?,?>>>{

    @Override
    public Optional<PieceEnPassantAction<?,?,?,?>> apply(Action<?> action) {
        if (Action.Type.EN_PASSANT.equals(action.getType())) {
            return Optional.of((PieceEnPassantAction<?,?,?,?>) action);
        }

        return Optional.empty();
    }
}