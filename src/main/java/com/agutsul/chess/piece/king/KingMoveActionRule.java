package com.agutsul.chess.piece.king;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.action.PieceMovePositionActionRule;

class KingMoveActionRule<COLOR extends Color,
                         KING extends KingPiece<COLOR>>
        extends PieceMovePositionActionRule<COLOR,KING> {

    KingMoveActionRule(Board board,
                       MovePieceAlgo<COLOR,KING,Position> algo) {

        super(Action.Type.MOVE, board, algo);
    }

    @Override
    protected Collection<PieceMoveAction<COLOR,KING>>
            createActions(KING king, Collection<Calculated> nextPositions) {

        var attackerColor = king.getColor().invert();

        var actions = new ArrayList<PieceMoveAction<COLOR,KING>>();
        for (var entry : nextPositions) {
            var position = (Position) entry;

            if (board.isAttacked(position, attackerColor)) {
                continue;
            }

            if (board.isMonitored(position, attackerColor)) {
                continue;
            }

            actions.add(new PieceMoveAction<>(king, position));
        }

        return actions;
    }
}