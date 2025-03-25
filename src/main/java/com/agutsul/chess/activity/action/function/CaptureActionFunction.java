package com.agutsul.chess.activity.action.function;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isPromote;

import java.util.Optional;
import java.util.function.Function;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;

final class CaptureActionFunction
        implements Function<Action<?>,Optional<PieceCaptureAction<?,?,?,?>>> {

    @Override
    public Optional<PieceCaptureAction<?,?,?,?>> apply(Action<?> action) {
        if (isCapture(action)) {
            return Optional.of((PieceCaptureAction<?,?,?,?>) action);
        }

        if (isPromote(action)) {
            var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();

            if (isCapture((Action<?>) sourceAction)) {
                return Optional.of((PieceCaptureAction<?,?,?,?>) sourceAction);
            }
        }

        return Optional.empty();
    }
}