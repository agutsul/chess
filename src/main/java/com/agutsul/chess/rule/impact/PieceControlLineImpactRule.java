package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public final class PieceControlLineImpactRule<COLOR extends Color,
                                              PIECE extends Piece<COLOR> & Capturable>
        extends AbstractControlImpactRule<COLOR,PIECE,
                                          PieceControlImpact<COLOR,PIECE>> {

    private final CapturePieceAlgo<COLOR,PIECE,Line> algo;

    public PieceControlLineImpactRule(Board board,
                                      CapturePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        var lines = algo.calculate(piece);

        var positions = new ArrayList<Calculated>();
        for (var line : lines) {
            for (var position : line) {
                var optionalPiece = board.getPiece(position);
                if (optionalPiece.isPresent()) {
                    var foundPiece = optionalPiece.get();
                    if (!Objects.equals(foundPiece.getColor(), piece.getColor())) {
                        positions.add(position);
                    }

                    break;
                }

                positions.add(position);
            }
        }

        return positions;
    }

    @Override
    protected Collection<PieceControlImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculated> positions) {

        var impacts = Stream.of(positions)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .map(position -> new PieceControlImpact<>(piece, position))
                .collect(toList());

        return impacts;
    }
}