package com.agutsul.chess.ai;

import static com.agutsul.chess.activity.action.Action.isPromote;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.adapter.Adapter;

final class ActionAdapter
        implements Adapter<Action<?>,Collection<Action<?>>> {

    private final Adapter<PiecePromoteAction<?,?>,Collection<Action<?>>> adapter;

    ActionAdapter() {
        this(new PromoteActionAdapter());
    }

    ActionAdapter(Adapter<PiecePromoteAction<?,?>,Collection<Action<?>>> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Collection<Action<?>> adapt(Action<?> action) {
        // replace origin promote action with pre-generated ones
        // containing promoted piece type because action selection
        // should be evaluated with all possible piece types:
        // BISHOP, ROOK, KNIGHT, QUEEN

        return isPromote(action)
                ? adapter.adapt((PiecePromoteAction<?,?>) action)
                : List.of(action);
    }
}