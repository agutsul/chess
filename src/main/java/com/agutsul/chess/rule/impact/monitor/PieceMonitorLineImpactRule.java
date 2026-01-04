package com.agutsul.chess.rule.impact.monitor;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceMonitorImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;

public final class PieceMonitorLineImpactRule<COLOR extends Color,
                                              PIECE extends Piece<COLOR> & Capturable & Lineable>
        extends AbstractMonitorImpactRule<COLOR,PIECE,
                                          PieceMonitorImpact<COLOR,PIECE>> {

    private final CapturePieceAlgo<COLOR,PIECE,Line> algo;

    public PieceMonitorLineImpactRule(Board board,
                                      CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        var lines = algo.calculate(piece);
        var opponentKing = board.getKing(piece.getColor().invert());

        // positions behind opponent king
        Collection<Calculatable> monitoredLines = Stream.of(opponentKing)
                .flatMap(Optional::stream)
                .map(KingPiece::getPosition)
                .flatMap(kingPosition -> Stream.of(lines)
                        .flatMap(Collection::stream)
                        .filter(line -> line.contains(kingPosition))
                        .map(line -> line.split(kingPosition))
                        .flatMap(Collection::stream)
                        .filter(not(Collection::isEmpty))
                        .filter(line -> !line.contains(piece.getPosition()))
                )
                .collect(toList());

        return monitoredLines;
    }

    @Override
    protected Collection<PieceMonitorImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculatable> lines) {

        var impacts = Stream.of(lines)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(Collection::stream)
                .map(position -> new PieceMonitorImpact<>(piece, position))
                .collect(toList());

        return impacts;
    }
}