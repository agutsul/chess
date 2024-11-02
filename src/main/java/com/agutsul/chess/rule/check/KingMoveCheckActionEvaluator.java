package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.HashSet;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;

final class KingMoveCheckActionEvaluator<COLOR extends Color,
                                         KING extends KingPiece<COLOR>>
        extends AbstractMoveCheckActionEvaluator<COLOR, KING> {

    KingMoveCheckActionEvaluator(Board board,
                                 Collection<Action<?>> pieceActions) {
        super(board, pieceActions);
    }

    @Override
    Collection<Action<?>> process(KING king,
                                  Collection<PieceCaptureAction<?,?,?,?>> checkActions,
                                  Collection<PieceMoveAction<?,?>> pieceMoveActions) {

        var attackerColor = king.getColor().invert();

        var actions = new HashSet<Action<?>>();
        for (var checkedAction : checkActions) {
            var attackLine = checkedAction.getAttackLine();

            for (var pieceMoveAction : pieceMoveActions) {
                var targetPosition = pieceMoveAction.getPosition();
                if (attackLine.contains(targetPosition)) {
                    continue;
                }

                if (!board.isAttacked(targetPosition, attackerColor)
                        && !board.isMonitored(targetPosition, attackerColor)) {

                    actions.add(pieceMoveAction);
                }
            }
        }

        return actions;
    }
}