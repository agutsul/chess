package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public final class PieceProtectLineImpactRule<COLOR extends Color,
                                              PIECE1 extends Piece<COLOR> & Capturable,
                                              PIECE2 extends Piece<COLOR>>
        extends AbstractProtectImpactRule<COLOR,PIECE1,PIECE2,
                                          PieceProtectImpact<COLOR,PIECE1,PIECE2>> {

    private final CapturePieceAlgo<COLOR,PIECE1,Line> algo;

    public PieceProtectLineImpactRule(Board board,
                                      CapturePieceAlgo<COLOR,PIECE1,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        var lines = algo.calculate(piece);

        var protectLines = new ArrayList<Calculated>();
        for (var line : lines) {
            var protectPositions = new ArrayList<Position>();
            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isPresent()) {
                    var otherPiece = optionalPiece.get();
                    if (Objects.equals(piece.getColor(), otherPiece.getColor())) {
                        protectPositions.add(position);
                    }

                    break;
                }
            }

            if (!protectPositions.isEmpty()) {
                protectLines.add(new Line(protectPositions));
            }
        }

        return protectLines;
    }

    @Override
    protected Collection<PieceProtectImpact<COLOR,PIECE1,PIECE2>>
            createImpacts(PIECE1 piece, Collection<Calculated> lines) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(lines)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(Collection::stream)
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(protectedPiece -> Objects.equals(protectedPiece.getColor(), piece.getColor()))
                .map(protectedPiece -> new PieceProtectImpact<>(piece, (PIECE2) protectedPiece))
                .collect(toList());

        return impacts;
    }
}