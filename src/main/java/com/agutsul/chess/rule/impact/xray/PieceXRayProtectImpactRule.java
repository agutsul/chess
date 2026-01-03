package com.agutsul.chess.rule.impact.xray;

import static com.agutsul.chess.rule.impact.LineImpactRule.containsPattern;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceXRayProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

final class PieceXRayProtectImpactRule<COLOR1 extends Color,
                                       COLOR2 extends Color,
                                       SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                       TARGET extends Piece<COLOR1>>
        extends AbstractXRayImpactRule<COLOR1,COLOR2,SOURCE,TARGET,
                                       PieceXRayProtectImpact<COLOR1,COLOR2,SOURCE,TARGET>> {

    private final Algo<SOURCE,Collection<Line>> algo;

    PieceXRayProtectImpactRule(Board board,
                               Algo<SOURCE,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Line> calculate(SOURCE piece) {
        return algo.calculate(piece);
    }

    @Override
    protected Collection<PieceXRayProtectImpact<COLOR1,COLOR2,SOURCE,TARGET>>
            createImpacts(SOURCE piece, Collection<Line> next) {

        var opponentColor = piece.getColor().invert();
        var attackedPieces = Stream.of(board.getImpacts(piece, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(Impact::getPosition)
                .map(attackedPosition -> Stream.of(board.getPieces(opponentColor))
                        .flatMap(Collection::stream)
                        .filter(opponentPiece -> Objects.equals(attackedPosition, opponentPiece.getPosition()))
                        .findFirst()
                )
                .flatMap(Optional::stream)
                .toList();

        if (attackedPieces.isEmpty()) {
            return emptyList();
        }

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(line -> {
                    var linePieces = board.getPieces(line);
                    if (linePieces.size() < 3) {
                        return null;
                    }

                    var optionalPiece = Stream.of(linePieces)
                            .flatMap(Collection::stream)
                            .filter(attacked -> !Objects.equals(piece, attacked))
                            .filter(attacked -> attackedPieces.contains(attacked))
                            .findFirst();

                    if (optionalPiece.isEmpty()) {
                        return null;
                    }

                    var attackedPiece = optionalPiece.get();
                    var impact = Stream.of(linePieces)
                            .flatMap(Collection::stream)
                            .filter(defended -> !Objects.equals(piece, defended))
                            .filter(defended -> !attackedPieces.contains(defended))
                            .filter(defended -> Objects.equals(defended.getColor(), piece.getColor()))
                            .filter(defended -> containsPattern(linePieces, List.of(piece, attackedPiece, defended)))
                            .findFirst()
                            .map(defended -> new PieceXRayProtectImpact<>(piece, (TARGET) defended, attackedPiece, line))
                            .orElse(null);

                    return impact;
                })
                .filter(Objects::nonNull)
                .map(impact -> (PieceXRayProtectImpact<COLOR1,COLOR2,SOURCE,TARGET>) impact)
                .collect(toList());

        return impacts;
    }
}