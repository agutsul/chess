package com.agutsul.chess.action.function;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PiecePromoteAction;

public final class CaptureActionFunction
        implements Function<Action<?>, Optional<PieceCaptureAction<?,?,?,?>>> {

    @Override
    public Optional<PieceCaptureAction<?,?,?,?>> apply(Action<?> action) {
        if (Action.Type.CAPTURE.equals(action.getType())
                || Action.Type.EN_PASSANT.equals(action.getType())) {

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