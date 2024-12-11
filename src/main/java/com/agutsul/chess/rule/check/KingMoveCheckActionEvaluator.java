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
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

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

            var checkerMonitoredPositions = getMonitoredPositions(checkedAction.getSource());

            for (var pieceMoveAction : pieceMoveActions) {
                var targetPosition = pieceMoveAction.getPosition();

                // skip moves on positions inside attack line
                if (attackLine.contains(targetPosition)) {
                    continue;
                }

                // skip positions attacked by any opponent piece
                var isAttacked = board.isAttacked(targetPosition, attackerColor);
                if (!isAttacked) {

                    // skip monitored positions
                    // for example positions behind the king but on the same attack line
                    var isMonitored = board.isMonitored(targetPosition, attackerColor);
                    if (!isMonitored) {
                        actions.add(pieceMoveAction);
                        continue;
                    }

                    // skip positions monitored by the check maker.
                    // position can be monitored by some other opponent piece
                    // but as soon as position not directly attacked by the opponent piece
                    // it is valid for move action
                    if (!checkerMonitoredPositions.contains(targetPosition)) {
                        actions.add(pieceMoveAction);
                    }
                }
            }
        }

        return actions;
    }

    private Collection<Position> getMonitoredPositions(Piece<?> piece) {
        return board.getImpacts(piece).stream()
                .filter(impact -> Impact.Type.MONITOR.equals(impact.getType()))
                .map(impact -> (PieceMonitorImpact<?,?>) impact)
                .map(PieceMonitorImpact::getPosition)
                .toList();
    }
}