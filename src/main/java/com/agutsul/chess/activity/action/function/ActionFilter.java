package com.agutsul.chess.activity.action.function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceBigMoveAction;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.PiecePromoteAction;

public final class ActionFilter<ACTION extends Action<?>>
        implements Function<Collection<Action<?>>, Collection<ACTION>> {

    private static final Logger LOGGER = getLogger(ActionFilter.class);

    private final Class<ACTION> actionClass;

    public ActionFilter(Class<ACTION> actionClass) {
        this.actionClass = actionClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<ACTION> apply(Collection<Action<?>> actions) {
        if (isEmpty(actions)) {
            return emptyList();
        }

        var function = ActionFunctionMapper.get((Class<Action<?>>) this.actionClass);
        if (function == null) {
            LOGGER.warn("Unknown filtration for action class: '{}'",
                    this.actionClass.getName()
            );

            return emptyList();
        }

        var filteredActions = actions.stream()
                .map(action -> ((Function<Action<?>,Optional<ACTION>>) function).apply(action))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return filteredActions;
    }

    private enum ActionFunctionMapper {
        MOVE_MODE(PieceMoveAction.class,            new MoveActionFunction()),
        BIG_MOVE_MODE(PieceBigMoveAction.class,     new BigMoveActionFunction()),
        CAPTURE_MODE(PieceCaptureAction.class,      new CaptureActionFunction()),
        EN_PASSANT_MODE(PieceEnPassantAction.class, new EnPassantActionFunction()),
        CASTLING_MODE(PieceCastlingAction.class,    new CastlingActionFunction()),
        PROMOTE_MODE(PiecePromoteAction.class,      new PromoteActionFunction());

        private final static Map<Class<Action<?>>, Function<Action<?>,?>> MAPPER = Stream.of(values())
                .collect(toMap(ActionFunctionMapper::actionClass, ActionFunctionMapper::function));

        private Class<Action<?>> actionClass;
        private Function<Action<?>,?> function;

        @SuppressWarnings("unchecked")
        ActionFunctionMapper(Class<?> actionClass, Function<Action<?>,?> function) {
            this.actionClass = (Class<Action<?>>) actionClass;
            this.function = function;
        }

        Class<Action<?>> actionClass() {
            return actionClass;
        }

        Function<Action<?>,?> function() {
            return function;
        }

        static Function<Action<?>,?> get(Class<Action<?>> actionClass) {
            return MAPPER.get(actionClass);
        }
    }
}