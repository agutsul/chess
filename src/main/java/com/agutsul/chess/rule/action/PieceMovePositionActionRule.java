package com.agutsul.chess.rule.action;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
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
    protected Collection<Calculated> calculate(PIECE piece) {
        var positions = algo.calculate(piece);
        return positions.stream()
                .filter(position -> board.isEmpty(position))
                .collect(toList());
    }

    @Override
    protected Collection<PieceMoveAction<COLOR,PIECE>>
            createActions(PIECE piece, Collection<Calculated> next) {

        var actions = new ArrayList<PieceMoveAction<COLOR,PIECE>>();
        for (var entry : next) {
            actions.add(createAction(piece, (Position) entry));
        }

        return actions;
    }

    protected PieceMoveAction<COLOR,PIECE> createAction(PIECE piece, Position position) {
        return new PieceMoveAction<>(piece, position);
    }
}