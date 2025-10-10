package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

final class PieceAbsoluteDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                    COLOR2 extends Color,
                                                    PIECE extends Piece<COLOR1>,
                                                    ATTACKER extends Piece<COLOR1> & Capturable,
                                                    ATTACKED extends KingPiece<COLOR2>>
        extends AbstractPieceDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                        PieceAbsoluteDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>> {

    PieceAbsoluteDiscoveredAttackImpactRule(Board board, Algo<PIECE,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceAbsoluteDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED>>
            createImpacts(PIECE piece, Collection<Line> lines) {

        var opponentColor = piece.getColor().invert();
        var optionalKing = board.getKing(opponentColor);
        if (optionalKing.isEmpty()) {
            return emptyList();
        }

        var opponentKing = (ATTACKED) optionalKing.get();
        var impacts = lines.stream()
                .filter(line -> line.contains(opponentKing.getPosition()))
                .map(line -> {
                    var linePieces = line.stream()
                            .map(position -> board.getPiece(position))
                            .flatMap(Optional::stream)
                            .toList();

                    if (linePieces.size() < 3) {
                        return null;
                    }

                    var impact = linePieces.stream()
                            .filter(attacker -> Objects.equals(piece.getColor(), attacker.getColor()))
                            .filter(attacker -> LINE_ATTACK_PIECE_TYPES.contains(attacker.getType()))
                            // searched pattern: 'attacker - piece - attacked king' or reverse
                            .filter(attacker -> containsPattern(linePieces, List.of(attacker, piece, opponentKing)))
                            .filter(attacker -> {
                                // check if piece is protected by line attacker
                                var protectImpacts = board.getImpacts(attacker, Impact.Type.PROTECT);
                                var isPieceProtected = protectImpacts.stream()
                                        .map(Impact::getPosition)
                                        .anyMatch(position -> Objects.equals(position, piece.getPosition()));

                                return isPieceProtected;
                            })
                            .map(attacker -> new PieceAbsoluteDiscoveredAttackImpact<>(piece, (ATTACKER) attacker, opponentKing, line))
                            .findFirst()
                            .orElse(null);

                    return impact;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return impacts;
    }
}