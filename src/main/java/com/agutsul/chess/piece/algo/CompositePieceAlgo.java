package com.agutsul.chess.piece.algo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

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
        this.algos = Stream.of(List.of(algo), List.of(additionalAlgos))
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public Collection<RESULT> calculate(SOURCE source) {
        var results = this.algos.stream()
                .map(algo -> algo.calculate(source))
                .flatMap(Collection::stream)
                .toList();

        return results;
    }
}