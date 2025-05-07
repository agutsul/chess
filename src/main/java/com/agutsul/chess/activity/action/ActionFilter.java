package com.agutsul.chess.activity.action;

import static com.agutsul.chess.activity.action.Action.isBigMove;
import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isCastling;
import static com.agutsul.chess.activity.action.Action.isEnPassant;
import static com.agutsul.chess.activity.action.Action.isMove;
import static com.agutsul.chess.activity.action.Action.isPromote;
import static com.agutsul.chess.activity.action.ActionFilter.ActionFilterFunction.function;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;

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

        var filter = function((Class<Action<?>>) this.actionClass);
        if (filter == null) {
            LOGGER.warn("Unknown filtration for action class: '{}'",
                    this.actionClass.getName()
            );

            return emptyList();
        }

        var filteredActions = actions.stream()
                .map(action -> (Optional<ACTION>) filter.apply(action))
                .flatMap(Optional::stream)
                .toList();

        return filteredActions;
    }

    enum ActionFilterFunction implements Function<Action<?>,Optional<? extends Action<?>>> {

        MOVE_MODE(PieceMoveAction.class) {
            @Override
            public Optional<PieceMoveAction<?,?>> apply(Action<?> action) {
                if (isMove(action)) {
                    return Optional.of((PieceMoveAction<?,?>) action);
                }

                if (isPromote(action)) {
                    return apply((Action<?>) ((PiecePromoteAction<?,?>) action).getSource());
                }

                return Optional.empty();
            }
        },
        BIG_MOVE_MODE(PieceBigMoveAction.class) {
            @Override
            public Optional<PieceBigMoveAction<?,?>> apply(Action<?> action) {
                return Optional.ofNullable(isBigMove(action)
                        ? (PieceBigMoveAction<?,?>) action
                        : null
                );
            }
        },
        CAPTURE_MODE(PieceCaptureAction.class) {
            @Override
            public Optional<PieceCaptureAction<?,?,?,?>> apply(Action<?> action) {
                if (isCapture(action)) {
                    return Optional.of((PieceCaptureAction<?,?,?,?>) action);
                }

                if (isPromote(action)) {
                    return apply((Action<?>) ((PiecePromoteAction<?,?>) action).getSource());
                }

                return Optional.empty();
            }
        },
        EN_PASSANT_MODE(PieceEnPassantAction.class) {
            @Override
            public Optional<PieceEnPassantAction<?,?,?,?>> apply(Action<?> action) {
                return Optional.ofNullable(isEnPassant(action)
                        ? (PieceEnPassantAction<?,?,?,?>) action
                        : null
                );
            }
        },
        CASTLING_MODE(PieceCastlingAction.class) {
            @Override
            public Optional<PieceCastlingAction<?,?,?>> apply(Action<?> action) {
                return Optional.ofNullable(isCastling(action)
                        ? (PieceCastlingAction<?,?,?>) action
                        : null
                );
            }
        },
        PROMOTE_MODE(PiecePromoteAction.class) {
            @Override
            public Optional<PiecePromoteAction<?,?>> apply(Action<?> action) {
                return Optional.ofNullable(isPromote(action)
                        ? (PiecePromoteAction<?,?>) action
                        : null
                );
            }
        };

        private final static Map<Class<Action<?>>,Function<Action<?>,Optional<? extends Action<?>>>> MAPPER =
                Stream.of(values()).collect(toMap(ActionFilterFunction::actionClass, identity()));

        private Class<Action<?>> actionClass;

        @SuppressWarnings("unchecked")
        ActionFilterFunction(Class<?> actionClass) {
            this.actionClass = (Class<Action<?>>) actionClass;
        }

        Class<Action<?>> actionClass() {
            return actionClass;
        }

        public static Function<Action<?>,Optional<? extends Action<?>>> function(Class<Action<?>> actionClass) {
            return MAPPER.get(actionClass);
        }
    }
}