package com.agutsul.chess.rule.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public abstract class AbstractMoveLineActionRule<COLOR extends Color,
                                                 PIECE extends Piece<COLOR> & Movable,
                                                 ACTION extends PieceMoveAction<COLOR, PIECE>>
        extends AbstractMoveActionRule<COLOR, PIECE, ACTION> {

    private final MovePieceAlgo<COLOR, PIECE, Line> algo;

    protected AbstractMoveLineActionRule(Board board, MovePieceAlgo<COLOR, PIECE, Line> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        var lines = algo.calculate(piece);

        var moveLines = new ArrayList<Calculated>();
        for (var line : lines) {
            var movePositions = new ArrayList<Position>();
            for (var position : line) {
                if (!board.isEmpty(position)) {
                    break;
                }
                movePositions.add(position);
            }

            if (!movePositions.isEmpty()) {
                moveLines.add(new Line(movePositions));
            }
        }

        return moveLines;
    }

    @Override
    protected Collection<ACTION> createActions(PIECE piece, Collection<Calculated> lines) {
        var actions = new ArrayList<ACTION>();
        for (var line : lines) {
            @SuppressWarnings("unchecked")
            var positions = (List<Position>) line;
            for (var position : positions) {
                actions.add(createAction(piece, position));
            }
        }

        return actions;
    }

    protected abstract ACTION createAction(PIECE piece, Position position);
}