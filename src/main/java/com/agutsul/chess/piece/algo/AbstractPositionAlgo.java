package com.agutsul.chess.piece.algo;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Positionable;
import com.agutsul.chess.board.Board;

public abstract class AbstractPositionAlgo<SOURCE extends Positionable,
                                           RESULT extends Calculatable>
        extends AbstractAlgo<SOURCE,RESULT>
        implements PositionAlgo<RESULT> {

    protected AbstractPositionAlgo(Board board) {
        super(board);
    }

    @Override
    public Collection<RESULT> calculate(SOURCE source) {
        // calculate for current piece position
        return calculate(source.getPosition());
    }
}