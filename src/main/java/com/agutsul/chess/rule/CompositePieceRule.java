package com.agutsul.chess.rule;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.piece.Piece;

public final class CompositePieceRule<RESULT extends Activity<?>,
                                      TYPE extends Enum<TYPE> & Activity.Type>
        implements Rule<Piece<?>, Collection<RESULT>> {

    private final List<AbstractRule<Piece<?>,?,TYPE>> rules = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public CompositePieceRule(Rule<? extends Piece<?>,?> rule,
                              Rule<? extends Piece<?>,?>... additionalRules) {

        register(rule);

        Stream.of(additionalRules)
            .forEach(ruleItem -> register(ruleItem));
    }

    @Override
    public Collection<RESULT> evaluate(Piece<?> piece) {
        return evaluate(this.rules, piece);
    }

    @SuppressWarnings("unchecked")
    public Collection<RESULT> evaluate(Piece<?> piece, TYPE type, TYPE... additionalTypes) {
        var types = new ArrayList<TYPE>();

        types.add(type);
        types.addAll(List.of(additionalTypes));

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

        var list = new ArrayList<RESULT>();
        for (var rule : rules) {
            var result = rule.evaluate(piece);
            list.addAll((Collection<RESULT>) result);
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    private void register(Rule<? extends Piece<?>,?> rule) {
        this.rules.add((AbstractRule<Piece<?>,?,TYPE>) rule);
    }
}