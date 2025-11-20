package com.agutsul.chess.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.Action;

final class CompositeResultMatcher<ACTION extends Action<?>,
                                   VALUE  extends Comparable<VALUE>,
                                   RESULT extends TaskResult<ACTION,VALUE>>
        implements ResultMatcher<ACTION,VALUE,RESULT> {

    private final List<ResultMatcher<ACTION,VALUE,RESULT>> matchers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    CompositeResultMatcher(ResultMatcher<ACTION,VALUE,RESULT> resultMatcher,
                           ResultMatcher<ACTION,VALUE,RESULT>... additionalResultMatchers) {

        Stream.of(List.of(resultMatcher), List.of(additionalResultMatchers))
            .flatMap(Collection::stream)
            .forEach(matcher -> this.matchers.add(matcher));
    }

    @Override
    public boolean match(RESULT result) {
        return this.matchers.stream()
                .anyMatch(matcher -> matcher.match(result));
    }
}