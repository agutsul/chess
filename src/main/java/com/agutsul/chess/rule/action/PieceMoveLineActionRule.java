package com.agutsul.chess.rule.action;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public class PieceMoveLineActionRule<COLOR extends Color,
                                     PIECE extends Piece<COLOR> & Movable>
        extends AbstractMoveActionRule<COLOR,PIECE,
                                       PieceMoveAction<COLOR,PIECE>> {

    private final MovePieceAlgo<COLOR,PIECE,Line> algo;

    public PieceMoveLineActionRule(Board board, MovePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board, Action.Type.MOVE);
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
    protected Collection<PieceMoveAction<COLOR,PIECE>>
            createActions(PIECE piece, Collection<Calculated> lines) {

        @SuppressWarnings("unchecked")
        var actions = Stream.of(lines)
                .flatMap(Collection::stream)
                .map(calculated -> (List<Position>) calculated)
                .flatMap(Collection::stream)
                .map(position -> new PieceMoveAction<>(piece, position))
                .collect(toList());

        return actions;
    }
}