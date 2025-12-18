package com.agutsul.chess.rule.impact.control;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

public final class PieceControlPositionImpactRule<COLOR extends Color,
                                                  PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractControlImpactRule<COLOR,PIECE,
                                          PieceControlImpact<COLOR,PIECE>> {

    private final CapturePieceAlgo<COLOR,PIECE,Position> algo;

    public PieceControlPositionImpactRule(Board board,
                                          CapturePieceAlgo<COLOR,PIECE,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceControlImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculatable> positions) {

        var impacts = Stream.of(positions)
                .flatMap(Collection::stream)
                .map(calculated -> new PieceControlImpact<>(piece, (Position) calculated))
                .collect(toList());

        return impacts;
    }
}