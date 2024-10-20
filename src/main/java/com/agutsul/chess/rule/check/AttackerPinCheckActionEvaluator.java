package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.HashSet;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

final class AttackerPinCheckActionEvaluator<COLOR extends Color,
                                            KING extends KingPiece<COLOR>>
        extends AbstractMoveCheckActionEvaluator<COLOR, KING> {

    AttackerPinCheckActionEvaluator(Board board,
                                    Collection<Action<?>> pieceActions) {
        super(board, pieceActions);
    }

    @Override
    Collection<Action<?>> process(KING king,
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