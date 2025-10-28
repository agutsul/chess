package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.piece.Piece.isKing;
import static com.agutsul.chess.position.LineFactory.createLine;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceMonitorImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public final class PieceMonitorLineImpactRule<COLOR extends Color,
                                              PIECE extends Piece<COLOR> & Capturable>
        extends AbstractMonitorImpactRule<COLOR,PIECE,
                                          PieceMonitorImpact<COLOR,PIECE>> {

    private final CapturePieceAlgo<COLOR,PIECE,Line> algo;

    public PieceMonitorLineImpactRule(Board board,
                                      CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        var lines = algo.calculate(piece);

        var monitorLines = new ArrayList<Calculated>();
        for (var line : lines) {
            var monitoredPositions = new ArrayList<Position>();

            // positions behind opponent king
            var isKingFound = false;
            for (var position : line) {
                if (!isKingFound) {
                    var optionalPiece = board.getPiece(position);
                    if (optionalPiece.isEmpty()) {
                        continue;
                    }

                    var foundPiece = optionalPiece.get();
                    if (!Objects.equals(foundPiece.getColor(), piece.getColor())) {
                        isKingFound = isKing(foundPiece);
                    } else {
                        break;
                    }
                }

                monitoredPositions.add(position);
            }

            if (!monitoredPositions.isEmpty()) {
                monitorLines.add(createLine(monitoredPositions));
            }
        }

        return monitorLines;
    }

    @Override
    protected Collection<PieceMonitorImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculated> lines) {

        var impacts = Stream.of(lines)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(Collection::stream)
                .map(position -> new PieceMonitorImpact<>(piece, position))
                .collect(toList());

        return impacts;
    }
}