package com.agutsul.chess.ai;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;

final class ActionValueComparator
        implements Comparator<ActionSimulationResult>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(ActionSimulationResult result1,ActionSimulationResult result2) {
        int compared = Integer.compare(result1.getValue(), result2.getValue());
        if (compared != 0) {
            return compared;
        }

        // compare actions
        return ObjectUtils.compare(result2.getAction(), result1.getAction());
    }
}