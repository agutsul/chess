package com.agutsul.chess.rule;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.agutsul.chess.piece.Piece;

public class CompositePieceRule<K,TYPE extends Enum<TYPE>>
        implements Rule<Piece<?>, Collection<K>> {

    private final List<Pair<TYPE,Rule<Piece<?>,?>>> rules;

    @SuppressWarnings("unchecked")
    public CompositePieceRule(Rule<? extends Piece<?>,?> rule,
                              Rule<? extends Piece<?>,?>... additionalRules) {

        var rules = new ArrayList<Pair<TYPE,Rule<Piece<?>,?>>>();

        registerRule(rules, rule);
        Stream.of(additionalRules).forEach(rl -> registerRule(rules, rl));

        this.rules = rules;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<K> evaluate(Piece<?> piece) {
        var list = new ArrayList<K>();

        for (var ruleEntry : this.rules) {
            var rule = ruleEntry.getValue();
            var result = rule.evaluate(piece);

            list.addAll((Collection<K>) result);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public Collection<K> evaluate(Piece<?> piece, TYPE type) {
        var typeRules = this.rules.stream()
                .filter(entry -> Objects.equals(entry.getKey(), type))
                .map(entry -> entry.getValue())
                .toList();

        if (typeRules.isEmpty()) {
            return emptyList();
        }

        var list = new ArrayList<K>();
        for (var rule : typeRules) {
            var result = rule.evaluate(piece);
            list.addAll((Collection<K>) result);
        }

        return list;
    }

    private void registerRule(List<Pair<TYPE,Rule<Piece<?>,?>>> rules,
                              Rule<? extends Piece<?>,?> rule) {

        @SuppressWarnings("unchecked")
        var aRule = (AbstractRule<Piece<?>,?,TYPE>) rule;
        rules.add(Pair.of(aRule.getType(), aRule));
    }
}