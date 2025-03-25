package com.agutsul.chess.activity.action.function;

import static com.agutsul.chess.activity.action.Action.isMove;
import static com.agutsul.chess.activity.action.Action.isPromote;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;

final class MoveActionFunction
        implements Function<Action<?>,Optional<PieceMoveAction<?,?>>> {

    @Override
    public Optional<PieceMoveAction<?,?>> apply(Action<?> action) {
        if (isMove(action)) {
            return Optional.of((PieceMoveAction<?,?>) action);
        }

        if (isPromote(action)) {
            var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();

            if (isMove((Action<?>) sourceAction)) {
                return Optional.of((PieceMoveAction<?,?>) sourceAction);
            }
        }

        return Optional.empty();
    }
}