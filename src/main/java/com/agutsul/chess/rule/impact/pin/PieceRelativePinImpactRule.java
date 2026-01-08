package com.agutsul.chess.rule.impact.pin;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

final class PieceRelativePinImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       PINNED extends Piece<COLOR1> & Pinnable,
                                       PIECE extends Piece<COLOR1>,
                                       ATTACKER extends Piece<COLOR2> & Capturable & Lineable>
        extends AbstractPinModeImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,
                                               PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>> {

    PieceRelativePinImpactRule(Board board) {
        super(board);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
            createImpacts(PINNED piece, Collection<Calculatable> next) {

        var valuablePieces = Stream.of(board.getPieces(piece.getColor()))
                .flatMap(Collection::stream)
                .filter(not(Piece::isKing))
                .filter(vp -> !Objects.equals(piece, vp))
                .filter(vp -> Math.abs(vp.getValue()) > Math.abs(piece.getValue()))
                .map(vp -> (PIECE) vp)
                .collect(toList());

        var impactLines = new ArrayListValuedHashMap<Line,PIECE>();
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

        Collection<PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>> impacts = Stream.of(impactLines.entries())
                .flatMap(Collection::stream)
                .map(entry -> {
                    var line = entry.getKey();

                    var linePieces = board.getPieces(line);
                    if (linePieces.size() < 3) {
                        return null;
                    }

                    var valuablePiece = entry.getValue();
                    var impact = Stream.of(linePieces)
                            .flatMap(Collection::stream)
                            .filter(Piece::isLinear)
                            .filter(attacker -> !Objects.equals(attacker.getColor(), piece.getColor()))
                            // searched pattern: 'attacker - pinned piece - valuable piece' or reverse
                            .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, valuablePiece)))
                            .filter(attacker -> {
                                // check if piece is attacked by line attacker
                                var isPieceAttacked = Stream.of(board.getImpacts(attacker, Impact.Type.CONTROL))
                                        .flatMap(Collection::stream)
                                        .map(Impact::getPosition)
                                        .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                                return isPieceAttacked;
                            })
                            .findFirst()
                            .map(attacker -> new PieceRelativePinImpact<>(piece, valuablePiece, (ATTACKER) attacker, line))
                            .orElse(null);

                    return impact;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());

        return impacts;
    }
}