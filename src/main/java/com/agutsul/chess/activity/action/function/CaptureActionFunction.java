package com.agutsul.chess.activity.action.function;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;

final class CaptureActionFunction
        implements Function<Action<?>, Optional<PieceCaptureAction<?,?,?,?>>> {

    @Override
    public Optional<PieceCaptureAction<?,?,?,?>> apply(Action<?> action) {
        if (Action.Type.CAPTURE.equals(action.getType())) {
            return Optional.of((PieceCaptureAction<?,?,?,?>) action);
        }

        if (Action.Type.PROMOTE.equals(action.getType())) {
            var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();

            if (Action.Type.CAPTURE.equals(sourceAction.getType())) {
                return Optional.of((PieceCaptureAction<?,?,?,?>) sourceAction);
            }
        }

        return Optional.empty();
    }
}