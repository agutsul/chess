package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.activity.impact.PieceRelativePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

final class PieceRelativePinImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       PINNED extends Piece<COLOR1> & Pinnable,
                                       PIECE  extends Piece<COLOR1>,
                                       ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractPiecePinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER> {

    PieceRelativePinImpactRule(Board board, Algo<PINNED,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
            createImpacts(PINNED piece, Collection<Line> lines) {

        var valuablePieces = board.getPieces(piece.getColor()).stream()
                .filter(not(Piece::isKing))
                .filter(vp -> !Objects.equals(piece, vp))
                .filter(vp -> Math.abs(vp.getValue()) > Math.abs(piece.getValue()))
                .map(vp -> (PIECE) vp)
                .collect(toList());

        MultiValuedMap<Line,PIECE> impactLines = new ArrayListValuedHashMap<>();
        Stream.of(lines)
            .flatMap(Collection::stream)
            .filter(line  -> line.contains(piece.getPosition()))
            .forEach(line -> valuablePieces.stream()
                    .filter(vp  -> line.contains(vp.getPosition()))
                    .forEach(vp -> impactLines.put(line, vp))
            );

        if (impactLines.isEmpty()) {
            return emptyList();
        }

        var impacts =
                impactLines.entries().stream()
                    .map(entry -> {
                        var line = entry.getKey();

                        var linePieces = line.stream()
                                .map(position -> board.getPiece(position))
                                .flatMap(Optional::stream)
                                .toList();

                        if (linePieces.size() < 3) {
                            return null;
                        }

                        var valuablePiece = entry.getValue();
                        var impact = linePieces.stream()
                                .filter(attacker -> attacker.getColor() != piece.getColor())
                                .filter(attacker -> LINE_ATTACK_PIECE_TYPES.contains(attacker.getType()))
                                // searched pattern: 'attacker - pinned piece - valuable piece' or reverse
                                .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, valuablePiece)))
                                .filter(attacker -> {
                                    // check if piece is attacked by line attacker
                                    var attackerImpacts = attacker.getImpacts(Impact.Type.CONTROL);
                                    var isPieceAttacked = attackerImpacts.stream()
                                            .map(Impact::getPosition)
                                            .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                                    return isPieceAttacked;
                                })
                                .findFirst()
                                .map(attacker -> (ATTACKER) attacker)
                                .map(attacker -> new PieceRelativePinImpact<>(piece, valuablePiece, attacker, line))
                                .orElse(null);

                        return impact;
                    })
                    .filter(Objects::nonNull)
                    .map(impact -> (PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>) impact)
                    .collect(toList());

        return impacts;
    }
}