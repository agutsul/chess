package com.agutsul.chess.piece.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public abstract class AbstractLineAlgo<SOURCE,RESULT>
        implements Algo<SOURCE,Collection<RESULT>> {

    protected final Board board;

    protected AbstractLineAlgo(Board board) {
        this.board = board;
    }

    Line calculateLine(Position current, int xStep, int yStep) {
        return new Line(calculateLine(current, new ArrayList<Position>(), xStep, yStep));
    }

    private List<Position> calculateLine(Position current, List<Position> positions, int x, int y) {
        var optionalNext = board.getPosition(current.x() + x, current.y() + y);
        if (optionalNext.isEmpty()) {
            return positions;
        }

        var nextPosition = optionalNext.get();
        positions.add(nextPosition);

        return calculateLine(nextPosition, positions, x, y);
    }
}