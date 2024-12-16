package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.HashSet;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

final class AttackerPinCheckActionEvaluator
        extends AbstractMoveCheckActionEvaluator {

    AttackerPinCheckActionEvaluator(Board board,
                                    Collection<Action<?>> pieceActions) {
        super(board, pieceActions);
    }

    @Override
    Collection<Action<?>> process(KingPiece<?> king,
                                  Collection<PieceCaptureAction<?,?,?,?>> checkActions,
                                  Collection<PieceMoveAction<?,?>> pieceMoveActions) {

        var actions = new HashSet<Action<?>>();
        for (var checkedAction : checkActions) {
            var attackLine = checkedAction.getAttackLine();

            for (var pieceMoveAction : pieceMoveActions) {
                if (attackLine.contains(pieceMoveAction.getPosition())) {
                    actions.add(pieceMoveAction);
                }
            }
        }

        return actions;
    }
}