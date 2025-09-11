package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public class PieceControlPositionImpactRule<COLOR extends Color,
                                            PIECE extends Piece<COLOR> & Capturable>
        extends AbstractControlImpactRule<COLOR,PIECE,
                                          PieceControlImpact<COLOR,PIECE>> {

    private final CapturePieceAlgo<COLOR,PIECE,Position> algo;

    public PieceControlPositionImpactRule(Board board,
                                          CapturePieceAlgo<COLOR,PIECE,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        return algo.calculate(piece).stream().collect(toList());
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