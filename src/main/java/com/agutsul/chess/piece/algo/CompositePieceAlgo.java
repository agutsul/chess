package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class CompositePieceAlgo<COLOR extends Color,
                                      SOURCE extends Piece<COLOR>,
                                      RESULT>
        extends AbstractAlgo<SOURCE,RESULT> {

    private final List<Algo<SOURCE,Collection<RESULT>>> algos;

    @SuppressWarnings("unchecked")
    public CompositePieceAlgo(Board board,
                              Algo<SOURCE,Collection<RESULT>> algo,
                              Algo<SOURCE,Collection<RESULT>>... additionalAlgos) {
        super(board);

        var algos = new ArrayList<Algo<SOURCE,Collection<RESULT>>>();
        algos.add(algo);
        algos.addAll(List.of(additionalAlgos));

        this.algos = algos;
    }

    @Override
    public Collection<RESULT> calculate(SOURCE source) {
        var results = new ArrayList<RESULT>();
        for (var algo : algos) {
            results.addAll(algo.calculate(source));
        }
        return results;
    }
}