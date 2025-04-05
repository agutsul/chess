package com.agutsul.chess.ai;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.activity.action.Action;

final class ActionValueComparator
        implements Comparator<Pair<Action<?>,Integer>>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Pair<Action<?>,Integer> pair1,Pair<Action<?>,Integer> pair2) {
        int compared = Integer.compare(pair1.getValue(), pair2.getValue());
        if (compared != 0) {
            return compared;
        }

        // compare actions
        return ObjectUtils.compare(pair2.getKey(), pair1.getKey());
    }
}