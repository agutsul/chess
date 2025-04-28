package com.agutsul.chess.ai;

import java.util.ArrayList;
import java.util.List;

import com.agutsul.chess.activity.action.Action;

final class CompositeResultMatcher<ACTION extends Action<?>,
                                   VALUE  extends Comparable<VALUE>,
                                   RESULT extends TaskResult<ACTION,VALUE>>
        implements ResultMatcher<ACTION,VALUE,RESULT> {

    private final List<ResultMatcher<ACTION,VALUE,RESULT>> matchers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    CompositeResultMatcher(ResultMatcher<ACTION,VALUE,RESULT> resultMatcher,
                           ResultMatcher<ACTION,VALUE,RESULT>... additionalResultMatchers) {

        this.matchers.add(resultMatcher);
        this.matchers.addAll(List.of(additionalResultMatchers));
    }

    @Override
    public boolean match(RESULT result) {
        for (var matcher : this.matchers) {
            if (matcher.match(result)) {
                return true;
            }
        }

        return false;
    }
}