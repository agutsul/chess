package com.agutsul.chess.rule.action;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceMovePositionActionRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Movable>
        extends AbstractMoveActionRule<COLOR,PIECE,
                                       PieceMoveAction<COLOR,PIECE>> {

    protected final MovePieceAlgo<COLOR,PIECE,Position> algo;

    public PieceMovePositionActionRule(Board board,
                                       MovePieceAlgo<COLOR,PIECE,Position> algo) {

        this(Action.Type.MOVE, board, algo);
    }

    public PieceMovePositionActionRule(Action.Type type, Board board,
                                       MovePieceAlgo<COLOR,PIECE,Position> algo) {

        super(board, type);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        Collection<Calculatable> positions = Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(position -> board.isEmpty(position))
                .collect(toList());

        return positions;
    }

    @Override
    protected Collection<PieceMoveAction<COLOR,PIECE>>
            createActions(PIECE piece, Collection<Calculatable> next) {

        var actions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> createAction(piece, (Position) calculated))
                .collect(toList());

        return actions;
    }

    protected PieceMoveAction<COLOR,PIECE> createAction(PIECE piece, Position position) {
        return new PieceMoveAction<>(piece, position);
    }
}