package com.agutsul.chess.action.function;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.PiecePromoteAction;

public class MoveActionFunction
        implements Function<Action<?>, Optional<PieceMoveAction<?,?>>>{

    @Override
    public Optional<PieceMoveAction<?,?>> apply(Action<?> action) {
        if (Action.Type.MOVE.equals(action.getType())) {
            return Optional.of((PieceMoveAction<?,?>) action);
        }

        if (Action.Type.PROMOTE.equals(action.getType())) {
            var sourceAction = ((PiecePromoteAction<?, ?>) action).getSource();
            if (Action.Type.MOVE.equals(sourceAction.getType())) {
                return Optional.of((PieceMoveAction<?,?>) sourceAction);
            }
        }

        return Optional.empty();
    }
}