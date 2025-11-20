package com.agutsul.chess.rule.action;

import static java.util.List.copyOf;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MoveLineAlgoAdapter;
import com.agutsul.chess.piece.algo.MovePieceAlgo;

public class PieceMoveLineActionRule<COLOR extends Color,
                                     PIECE extends Piece<COLOR> & Movable>
        extends AbstractMoveActionRule<COLOR,PIECE,
                                       PieceMoveAction<COLOR,PIECE>> {

    private final MovePieceAlgo<COLOR,PIECE,Line> algo;

    public PieceMoveLineActionRule(Board board, MovePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board, Action.Type.MOVE);
        this.algo = new MoveLineAlgoAdapter<>(board, algo);
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceMoveAction<COLOR,PIECE>>
            createActions(PIECE piece, Collection<Calculatable> lines) {

        var actions = Stream.of(lines)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(Collection::stream)
                .map(position -> new PieceMoveAction<>(piece, position))
                .collect(toList());

        return actions;
    }
}