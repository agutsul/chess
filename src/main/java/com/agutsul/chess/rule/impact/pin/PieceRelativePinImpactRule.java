package com.agutsul.chess.rule.impact.pin;

import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

final class PieceRelativePinImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       PINNED extends Piece<COLOR1> & Movable & Capturable & Pinnable,
                                       DEFENDED extends Piece<COLOR1>,
                                       ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends AbstractPinModeImpactRule<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER,
                                          PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER>> {

    PieceRelativePinImpactRule(Board board) {
        super(board);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER>>
            createImpacts(PINNED piece, Collection<Calculatable> next) {

        var valuablePieces = Stream.of(board.getPieces(piece.getColor()))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .filter(vp -> !Objects.equals(piece, vp))
                .filter(vp -> Math.abs(vp.getValue()) > Math.abs(piece.getValue()))
                .map(vp -> (DEFENDED) vp)
                .toList();

        var impactLines = new ArrayListValuedHashMap<Line,DEFENDED>();
        Stream.of(next)
            .flatMap(Collection::stream)
            .map(calculated -> (Line) calculated)
            .filter(line  -> line.contains(piece.getPosition()))
            .forEach(line -> valuablePieces.stream()
                    .filter(vp  -> line.contains(vp.getPosition()))
                    .forEach(vp -> impactLines.put(line, vp))
            );

        if (impactLines.isEmpty()) {
            return emptyList();
        }

        var impacts = Stream.of(impactLines.entries())
                .flatMap(Collection::stream)
                .map(entry -> createImpacts(piece, entry.getValue(), entry.getKey()))
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        return impacts;
    }

    @Override
    protected PiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER>
            createImpact(PINNED pinnedPiece, DEFENDED defendedPiece, ATTACKER attacker, Line line) {

        return new PieceRelativePinImpact<>(pinnedPiece, defendedPiece, attacker, line);
    }
}