package com.agutsul.chess.rule.action;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.LinePositionAlgoAdapter;
import com.agutsul.chess.piece.algo.MoveLineAlgoAdapter;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

public final class PieceMoveLineActionRule<COLOR extends Color,
                                           PIECE extends Piece<COLOR> & Movable & Lineable>
        extends AbstractMoveActionRule<COLOR,PIECE,PieceMoveAction<COLOR,PIECE>> {

    private final Algo<PIECE,Collection<Position>> algo;

    public PieceMoveLineActionRule(Board board,
                                   MovePieceAlgo<COLOR,PIECE,Line> algo) {
        super(board, Action.Type.MOVE);
        this.algo = new LinePositionAlgoAdapter<>(new MoveLineAlgoAdapter<>(board, algo));
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceMoveAction<COLOR,PIECE>>
            createActions(PIECE piece, Collection<Calculatable> positions) {

        var actions = Stream.of(positions)
                .flatMap(Collection::stream)
                .map(calculated -> new PieceMoveAction<>(piece, (Position) calculated))
                .toList();

        return actions;
    }
}