package com.agutsul.chess.piece.king;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.PieceMovePositionActionRule;

final class KingMoveActionRule<COLOR extends Color,
                               KING extends KingPiece<COLOR>>
        extends PieceMovePositionActionRule<COLOR,KING> {

    KingMoveActionRule(Board board,
                       MovePieceAlgo<COLOR,KING,Position> algo) {

        super(Action.Type.MOVE, board, algo);
    }

    @Override
    protected Collection<PieceMoveAction<COLOR,KING>>
            createActions(KING king, Collection<Calculated> next) {

        var attackerColor = king.getColor().invert();
        var actions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .filter(position -> !board.isAttacked(position, attackerColor))
                .filter(position -> !board.isMonitored(position, attackerColor))
                .map(position -> new PieceMoveAction<>(king, position))
                .collect(toList());

        return actions;
    }
}