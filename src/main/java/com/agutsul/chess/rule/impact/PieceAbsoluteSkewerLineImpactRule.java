package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAbsoluteSkewerImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

final class PieceAbsoluteSkewerLineImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                              ATTACKED extends KingPiece<COLOR2>,
                                              DEFENDED extends Piece<COLOR2>>
        extends AbstractPieceSkewerImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,
                                              PieceAbsoluteSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>> {

    PieceAbsoluteSkewerLineImpactRule(Board board, Algo<ATTACKER,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceAbsoluteSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
            createImpacts(ATTACKER piece, Collection<Line> lines) {

        var opponentColor = piece.getColor().invert();
        var optionalKing = board.getKing(opponentColor);
        if (optionalKing.isEmpty()) {
            return emptyList();
        }

        var king = (ATTACKED) optionalKing.get();

        // check if king is attacked by line attacker
        var attackerImpacts = board.getImpacts(piece, Impact.Type.CONTROL);
        var isKingAttacked = attackerImpacts.stream()
                .map(Impact::getPosition)
                .anyMatch(position -> Objects.equals(position, king.getPosition()));

        if (!isKingAttacked) {
            return emptyList();
        }

        var expectedPieces = List.of(piece, king);
        var impacts = lines.stream()
                .map(line -> {
                    var linePieces = line.stream()
                            .map(position -> board.getPiece(position))
                            .flatMap(Optional::stream)
                            .toList();

                    if (linePieces.size() < 3 || !linePieces.containsAll(expectedPieces)) {
                        return null;
                    }

                    var impact = linePieces.stream()
                            .filter(defended -> !Objects.equals(king,  defended))
                            .filter(defended -> !Objects.equals(piece, defended))
                            .filter(defended -> defended.getColor() != piece.getColor())
                            .filter(defended -> containsPattern(linePieces, List.of(piece, king, defended)))
                            .findFirst()
                            .map(defended -> new PieceAbsoluteSkewerImpact<>(piece, king, (DEFENDED) defended, line))
                            .orElse(null);

                    return impact;
                })
                .filter(Objects::nonNull)
                .collect(toList());

        return impacts;
    }
}