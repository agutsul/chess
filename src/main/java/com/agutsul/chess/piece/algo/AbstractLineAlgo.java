package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.position.Position;

abstract class AbstractLineAlgo<SOURCE,RESULT>
        implements Algo<SOURCE,Collection<RESULT>> {

    protected final Board board;

    protected AbstractLineAlgo(Board board) {
        this.board = board;
    }

    protected List<Position> calculate(Position current, int xStep, int yStep) {
        return calculate(board, current, xStep, yStep, new ArrayList<Position>());
    }

    private static List<Position> calculate(Board board, Position current, int x, int y,
                                            List<Position> positions) {

        return Stream.of(board.getPosition(current.x() + x, current.y() + y))
                .flatMap(Optional::stream)
                .peek(nextPosition -> positions.add(nextPosition))
                .map(nextPosition -> calculate(board, nextPosition, x, y, positions))
                .findFirst()
                .orElse(positions);
    }
}