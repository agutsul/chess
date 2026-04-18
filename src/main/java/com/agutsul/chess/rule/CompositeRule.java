package com.agutsul.chess.rule;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;

public final class CompositeRule<SOURCE extends Positionable,
                                 RESULT extends Activity<TYPE,?>,
                                 TYPE   extends Enum<TYPE> & Activity.Type>
        implements Rule<SOURCE,Collection<RESULT>> {

    private final List<AbstractRule<SOURCE,?,TYPE>> rules = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public CompositeRule(Rule<? extends SOURCE,?> rule,
                         Rule<? extends SOURCE,?>... additionalRules) {

        Stream.of(List.of(rule), List.of(additionalRules))
            .flatMap(Collection::stream)
            .forEach(this::register);
    }

    @Override
    public Collection<RESULT> evaluate(SOURCE source) {
        return evaluate(this.rules, source);
    }

    @SuppressWarnings("unchecked")
    public Collection<RESULT> evaluate(SOURCE source, TYPE type, TYPE... additionalTypes) {
        var types = Stream.of(List.of(type), List.of(additionalTypes))
                .flatMap(Collection::stream)
                .toList();

        var typeRules = Stream.of(rules)
                .flatMap(Collection::stream)
                .filter(rule -> types.contains(rule.getType()))
                .toList();

        if (typeRules.isEmpty()) {
            return emptyList();
        }

        return evaluate(typeRules, source);
    }

    @SuppressWarnings("unchecked")
    private Collection<RESULT> evaluate(Collection<? extends Rule<SOURCE,?>> rules,
                                        SOURCE source) {

        var result = Stream.of(rules)
                .flatMap(Collection::stream)
                .map(rule -> (Collection<RESULT>) rule.evaluate(source))
                .flatMap(Collection::stream)
                .toList();

        return result;
    }

    @SuppressWarnings("unchecked")
    private void register(Rule<? extends SOURCE,?> rule) {
        this.rules.add((AbstractRule<SOURCE,?,TYPE>) rule);
    }
}