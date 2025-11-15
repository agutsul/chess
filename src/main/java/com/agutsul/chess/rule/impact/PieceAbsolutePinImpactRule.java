package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.LineImpactRule.LINE_ATTACK_PIECE_TYPES;
import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsolutePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class PieceAbsolutePinImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       PINNED extends Piece<COLOR1> & Pinnable,
                                       KING extends KingPiece<COLOR1>,
                                       ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractPiecePinImpactRule<COLOR1,COLOR2,PINNED,KING,ATTACKER,
                                           PieceAbsolutePinImpact<COLOR1,COLOR2,PINNED,KING,ATTACKER>> {

    PieceAbsolutePinImpactRule(Board board) {
        super(board);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceAbsolutePinImpact<COLOR1,COLOR2,PINNED,KING,ATTACKER>>
            createImpacts(PINNED piece, Collection<Line> lines) {

        var optionalKing = board.getKing(piece.getColor());
        if (optionalKing.isEmpty()) {
            return emptyList();
        }

        var king = (KING) optionalKing.get();
        var impactLines = lines.stream()
                .filter(line -> line.contains(king.getPosition()))
                .filter(line -> line.contains(piece.getPosition()))
                .toList();

        if (impactLines.isEmpty()) {
            return emptyList();
        }

        var impacts = impactLines.stream()
                .map(line -> {
                    var linePieces = board.getPieces(line);
                    if (linePieces.size() < 3) {
                        return null;
                    }

                    var impact = linePieces.stream()
                            .filter(attacker -> !Objects.equals(attacker.getColor(), piece.getColor()))
                            .filter(attacker -> LINE_ATTACK_PIECE_TYPES.contains(attacker.getType()))
                            // searched pattern: 'attacker - pinned piece - king' or reverse
                            .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, king)))
                            .filter(attacker -> {
                                // check if piece is attacked by line attacker
                                var attackerImpacts = board.getImpacts(attacker, Impact.Type.CONTROL);
                                var isPieceAttacked = attackerImpacts.stream()
                                        .map(Impact::getPosition)
                                        .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                                return isPieceAttacked;
                            })
                            .filter(attacker -> {
                                // check if king is monitored by line attacker
                                var attackerImpacts = board.getImpacts(attacker, Impact.Type.MONITOR);
                                var isKingMonitored = attackerImpacts.stream()
                                        .map(Impact::getPosition)
                                        .anyMatch(position -> Objects.equals(position, king.getPosition()));

                                return isKingMonitored;
                            })
                            .findFirst()
                            .map(attacker -> new PieceAbsolutePinImpact<>(piece, king, (ATTACKER) attacker, line))
                            .orElse(null);

                    return impact;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());

        return impacts;
    }
}