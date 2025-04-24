package com.agutsul.chess.ai;

import static java.util.Collections.sort;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;

enum ActionSelectionFunction {

    WHITE_MODE(Colors.WHITE, actionValues -> actionValues.getLast()),  // max
    BLACK_MODE(Colors.BLACK, actionValues -> actionValues.getFirst()); // min

    private static final Map<Color,ActionSelectionFunction> MODES =
            Stream.of(values()).collect(toMap(ActionSelectionFunction::color,identity()));

    private static final Comparator<SimulationResult<Action<?>,Integer>> COMPARATOR =
            new ActionValueComparator<>();

    private Color color;
    private Function<List<SimulationResult<Action<?>,Integer>>,SimulationResult<Action<?>,Integer>> function;

    ActionSelectionFunction(Color color,
                            Function<List<SimulationResult<Action<?>,Integer>>,SimulationResult<Action<?>,Integer>> function) {

        this.color = color;
        this.function = function;
    }

    public SimulationResult<Action<?>,Integer> apply(List<SimulationResult<Action<?>,Integer>> actionValues) {
        sort(actionValues, COMPARATOR);
        return function.apply(actionValues);
    }

    private Color color() {
        return color;
    }

    public static ActionSelectionFunction of(Color color) {
        return MODES.get(color);
    }
}