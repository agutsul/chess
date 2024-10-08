package com.agutsul.chess.rule.action;

import static java.util.Collections.emptyList;

import java.util.Collection;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractMoveActionRule<COLOR extends Color,
                                      PIECE extends Piece<COLOR> & Movable,
                                      ACTION extends PieceMoveAction<COLOR, PIECE>>
        extends AbstractRule<PIECE, ACTION>
        implements MoveActionRule<COLOR, PIECE, ACTION> {

    protected AbstractMoveActionRule(Board board) {
        super(board);
    }

    @Override
    public final Collection<ACTION> evaluate(PIECE piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createActions(piece, next);
    }

    protected abstract Collection<Calculated> calculate(PIECE piece);

    protected abstract Collection<ACTION> createActions(PIECE piece,
                                                        Collection<Calculated> next);
}