package com.agutsul.chess.rule.action;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public abstract class AbstractMovePositionActionRule<COLOR extends Color,
                                                     PIECE extends Piece<COLOR> & Movable,
                                                     ACTION extends PieceMoveAction<COLOR,PIECE>>
        extends AbstractMoveActionRule<COLOR,PIECE,ACTION> {

    protected final MovePieceAlgo<COLOR,PIECE,Calculated> algo;

    protected AbstractMovePositionActionRule(Board board,
                                             MovePieceAlgo<COLOR,PIECE,Calculated> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        var positions = algo.calculate(piece);
        return positions.stream()
                .filter(position -> board.isEmpty((Position) position))
                .toList();
    }

    @Override
    protected Collection<ACTION> createActions(PIECE piece,
                                               Collection<Calculated> next) {
        var actions = new ArrayList<ACTION>();
        for (var entry : next) {
            actions.add(createAction(piece, (Position) entry));
        }

        return actions;
    }

    protected abstract ACTION createAction(PIECE piece, Position position);
}