package com.agutsul.chess.rule;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.piece.Piece;

public final class CompositePieceRule<RESULT extends Activity<TYPE,?>,
                                      TYPE extends Enum<TYPE> & Activity.Type>
        implements Rule<Piece<?>,Collection<RESULT>> {

    private final List<AbstractRule<Piece<?>,?,TYPE>> rules = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public CompositePieceRule(Rule<? extends Piece<?>,?> rule,
                              Rule<? extends Piece<?>,?>... additionalRules) {

        Stream.of(List.of(rule), List.of(additionalRules))
            .flatMap(Collection::stream)
            .forEach(this::register);
    }

    @Override
    public Collection<RESULT> evaluate(Piece<?> piece) {
        return evaluate(this.rules, piece);
    }

    @SuppressWarnings("unchecked")
    public Collection<RESULT> evaluate(Piece<?> piece, TYPE type, TYPE... additionalTypes) {
        var types = Stream.of(List.of(type), List.of(additionalTypes))
                .flatMap(Collection::stream)
                .toList();

        var typeRules = this.rules.stream()
                .filter(rule -> types.contains(rule.getType()))
                .toList();

        if (typeRules.isEmpty()) {
            return emptyList();
        }

        return evaluate(typeRules, piece);
    }

    @SuppressWarnings("unchecked")
    private Collection<RESULT> evaluate(Collection<? extends Rule<Piece<?>,?>> rules,
                                        Piece<?> piece) {

        var result = rules.stream()
                .map(rule -> (Collection<RESULT>) rule.evaluate(piece))
                .flatMap(Collection::stream)
                .toList();

        return result;
    }

    @SuppressWarnings("unchecked")
    private void register(Rule<? extends Piece<?>,?> rule) {
        this.rules.add((AbstractRule<Piece<?>,?,TYPE>) rule);
    }
}