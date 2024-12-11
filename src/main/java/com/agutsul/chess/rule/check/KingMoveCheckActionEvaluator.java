package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.HashSet;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.KingPiece;

final class KingMoveCheckActionEvaluator
        extends AbstractMoveCheckActionEvaluator {

    KingMoveCheckActionEvaluator(Board board,
                                 Collection<Action<?>> pieceActions) {
        super(board, pieceActions);
    }

    @Override
    Collection<Action<?>> process(KingPiece<?> king,
                                  Collection<PieceCaptureAction<?,?,?,?>> checkActions,
                                  Collection<PieceMoveAction<?,?>> pieceMoveActions) {

        var attackerColor = king.getColor().invert();

        var actions = new HashSet<Action<?>>();
        for (var checkedAction : checkActions) {
            var attackLine = checkedAction.getAttackLine();
            var checkedPiece = checkedAction.getSource();

            var checkedPieceMonitoringPositions = board.getImpacts(checkedPiece).stream()
                    .filter(impact -> Impact.Type.MONITOR.equals(impact.getType()))
                    .map(impact -> (PieceMonitorImpact<?,?>) impact)
                    .map(PieceMonitorImpact::getPosition)
                    .toList();

            for (var pieceMoveAction : pieceMoveActions) {
                var targetPosition = pieceMoveAction.getPosition();
                if (attackLine.contains(targetPosition)) {
                    continue;
                }

                var isAttacked = board.isAttacked(targetPosition, attackerColor);
                if (!isAttacked) {
                    var isMonitored = board.isMonitored(targetPosition, attackerColor);
                    if (!isMonitored) {
                        actions.add(pieceMoveAction);
                        continue;
                    }

                    // verify if checked piece is monitoring targetPosition
                    if (!checkedPieceMonitoringPositions.contains(targetPosition)) {
                        actions.add(pieceMoveAction);
                    }
                }
            }
        }

        return actions;
    }
}