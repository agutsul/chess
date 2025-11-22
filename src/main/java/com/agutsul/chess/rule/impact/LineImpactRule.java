package com.agutsul.chess.rule.impact;

import static java.util.Collections.indexOfSubList;
import static java.util.List.copyOf;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

interface LineImpactRule {

    static boolean containsPattern(Collection<Piece<Color>> pieces, List<Piece<?>> pattern) {
        return containsPattern(copyOf(pieces), pattern);
    }

    static boolean containsPattern(List<Piece<Color>> pieces, List<Piece<?>> pattern) {
        return hasPattern(pieces, pattern)
                || hasPattern(pieces, pattern.reversed());
    }

    static boolean hasPattern(List<Piece<Color>> pieces, List<Piece<?>> pattern) {
        return indexOfSubList(pieces, pattern) != -1;
    }
}