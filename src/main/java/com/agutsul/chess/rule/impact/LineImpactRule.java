package com.agutsul.chess.rule.impact;

import static java.util.Collections.indexOfSubList;
import static java.util.List.copyOf;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

interface LineImpactRule {

    Set<Piece.Type> LINE_ATTACK_PIECE_TYPES =
            EnumSet.of(Piece.Type.BISHOP, Piece.Type.ROOK, Piece.Type.QUEEN);

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