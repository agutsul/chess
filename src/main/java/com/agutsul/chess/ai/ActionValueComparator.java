package com.agutsul.chess.ai;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

final class ActionValueComparator<VALUE extends Comparable<VALUE>>
        implements Comparator<ActionSimulationResult<VALUE>>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(ActionSimulationResult<VALUE> result1,
                       ActionSimulationResult<VALUE> result2) {

        int compared = ObjectUtils.compare(result1.getValue(), result2.getValue());
        if (compared != 0) {
            return compared;
        }

        // compare actions
        return ObjectUtils.compare(result2.getAction(), result1.getAction());
    }
}