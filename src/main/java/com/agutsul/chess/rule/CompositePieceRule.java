package com.agutsul.chess.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.piece.Piece;

public class CompositePieceRule<K>
        implements Rule<Piece<?>, Collection<K>> {

    private final List<Rule<Piece<?>, ?>> rules;

    @SuppressWarnings("unchecked")
    public CompositePieceRule(Rule<? extends Piece<?>,?> rule,
                              Rule<? extends Piece<?>,?>... additionalRules) {

        var rules = new ArrayList<Rule<Piece<?>,?>>();
        rules.add((Rule<Piece<?>,?>) rule);

        Stream.of(additionalRules).forEach(r -> rules.add((Rule<Piece<?>,?>) r));

        this.rules = rules;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<K> evaluate(Piece<?> piece) {
        var list = new ArrayList<K>();
        for (var rule : rules) {
            list.addAll((Collection<K>) rule.evaluate(piece));
        }
        return list;
    }
}