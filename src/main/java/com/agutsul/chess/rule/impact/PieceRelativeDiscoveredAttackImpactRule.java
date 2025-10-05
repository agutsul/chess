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
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceRelativeDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

final class PieceRelativeDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                    COLOR2 extends Color,
                                                    PIECE extends Piece<COLOR1>,
                                                    ATTACKER extends Piece<COLOR1> & Capturable,
                                                    ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                        PieceRelativeDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>> {


    PieceRelativeDiscoveredAttackImpactRule(Board board, Algo<PIECE,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceRelativeDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
            createImpacts(PIECE piece, Collection<Line> lines) {

        var opponentColor = piece.getColor().invert();
        var opponentPieces = board.getPieces(opponentColor).stream()
                .filter(not(Piece::isKing))
                .map(vp -> (ATTACKED) vp)
                .collect(toList());

        MultiValuedMap<Line,ATTACKED> impactLines = new ArrayListValuedHashMap<>();
        Stream.of(lines)
            .flatMap(Collection::stream)
            .filter(line  -> line.contains(piece.getPosition()))
            .forEach(line -> opponentPieces.stream()
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

                        var opponentPiece = entry.getValue();
                        var impact = linePieces.stream()
                                .filter(attacker -> attacker.getColor() == piece.getColor())
                                .filter(attacker -> LINE_ATTACK_PIECE_TYPES.contains(attacker.getType()))
                                // searched pattern: 'attacker - piece - attacked piece' or reverse
                                .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, opponentPiece)))
                                .filter(attacker -> {
                                    // check if piece is protected by line attacker
                                    var protectImpacts = board.getImpacts(attacker, Impact.Type.PROTECT);
                                    var isPieceProtected = protectImpacts.stream()
                                            .map(Impact::getPosition)
                                            .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                                    return isPieceProtected;
                                })
                                .findFirst()
                                .map(attacker -> new PieceRelativeDiscoveredAttackImpact<>(piece, (ATTACKER) attacker, opponentPiece, line))
                                .orElse(null);

                        return impact;
                    })
                    .filter(Objects::nonNull)
                    .collect(toList());

        return impacts;
    }
}