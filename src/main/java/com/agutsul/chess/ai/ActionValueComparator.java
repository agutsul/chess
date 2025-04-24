package com.agutsul.chess.ai;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

import com.agutsul.chess.activity.action.Action;

final class ActionValueComparator<VALUE extends Comparable<VALUE>>
        implements Comparator<SimulationResult<Action<?>,VALUE>>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(SimulationResult<Action<?>,VALUE> result1,
                       SimulationResult<Action<?>,VALUE> result2) {

        int compared = ObjectUtils.compare(result1.getValue(), result2.getValue());
        if (compared != 0) {
            return compared;
        }

        // compare actions
        return ObjectUtils.compare(getAction(result2), getAction(result1));
    }

    private Action<?> getAction(SimulationResult<Action<?>,VALUE> result) {
        return ((ActionSimulationResult<VALUE>) result).getSimulated();
    }
}