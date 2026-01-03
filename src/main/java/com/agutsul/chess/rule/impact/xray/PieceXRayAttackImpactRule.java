package com.agutsul.chess.rule.impact.xray;

import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceXRayAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

final class PieceXRayAttackImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                      TARGET extends Piece<COLOR2>>
        extends AbstractXRayImpactRule<COLOR1,COLOR2,SOURCE,TARGET,
                                       PieceXRayAttackImpact<COLOR1,COLOR2,SOURCE,TARGET>> {

    private final Algo<SOURCE,Collection<Line>> algo;

    PieceXRayAttackImpactRule(Board board,
                              Algo<SOURCE,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Line> calculate(SOURCE piece) {
        var opponentColor = piece.getColor().invert();

        var lines = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .map(line -> line.split(piece.getPosition()))
                .flatMap(Collection::stream)
                .filter(not(Collection::isEmpty))
                .filter(line -> board.getPieces(line).size() >= 3)
                .filter(line -> !board.getPieces(opponentColor, line).isEmpty())
                .toList();

        return lines;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<PieceXRayAttackImpact<COLOR1,COLOR2,SOURCE,TARGET>>
            createImpacts(SOURCE piece, Collection<Line> next) {

        var impacts = new ArrayList<PieceXRayAttackImpact<COLOR1,COLOR2,SOURCE,TARGET>>();
        for (var line : next) {
            List<Piece<?>> linePieces = new ArrayList<>(board.getPieces(line));
            if (!Objects.equals(linePieces.getFirst(), piece)) {
                linePieces = linePieces.reversed();
            }

            var pieces = new LinkedHashSet<Piece<?>>();
            for (int i = 0, j = 2; j < linePieces.size(); j++) {
                var attacker = linePieces.get(i);
                pieces.add(linePieces.get(i + 1));

                var attacked = linePieces.get(j);
                if (!Objects.equals(attacker.getColor(), attacked.getColor())) {
                    impacts.add(new PieceXRayAttackImpact<>(
                            (SOURCE) attacker,
                            (TARGET) attacked,
                            new ArrayList<>(pieces),
                            line.subLine(attacker.getPosition(), attacked.getPosition())
                    ));
                }

                pieces.add(attacked);
            }
        }

        return impacts;
    }
}