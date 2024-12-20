package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashSet;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PieceMonitorImpact;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

final class KingMoveCheckMateEvaluator
        implements CheckMateEvaluator {

    private static final Logger LOGGER = getLogger(KingMoveCheckMateEvaluator.class);

    private final Board board;

    KingMoveCheckMateEvaluator(Board board) {
        this.board = board;
    }

    @Override
    public Boolean evaluate(KingPiece<?> king) {
        LOGGER.info("Evaluate king '{}' escape ability", king);

        var positions = new HashSet<Position>();
        var attackerColor = king.getColor().invert();

        var moveActions = board.getActions(king, Action.Type.MOVE);
        for (var action : moveActions) {
            var targetPosition = action.getPosition();

            var isAttacked = board.isAttacked(targetPosition, attackerColor);
            if (!isAttacked) {

                var isMonitored = board.isMonitored(targetPosition, attackerColor);
                if (!isMonitored) {
                    positions.add(targetPosition);
                    continue;
                }

                // check if there is pinned piece in between monitored position
                // and attacker monitoring that position.
                // So, actual attack is blocked and as result position should be available for move
                var pinnedPieces = board.getPieces(king.getColor()).stream()
                        .filter(piece -> !Piece.Type.KING.equals(piece.getType()))
                        .filter(piece -> ((Pinnable) piece).isPinned())
                        .toList();

                for (var piece : pinnedPieces) {
                    var pinImpacts = board.getImpacts(piece, Impact.Type.PIN);
                    var isBlocked = pinImpacts.stream()
                            .map(impact -> (PiecePinImpact<?,?,?,?,?>) impact)
                            .map(PiecePinImpact::getTarget)
                            .filter(checkImpact -> Objects.equals(checkImpact.getTarget(), king))
                            .map(PieceCheckImpact::getSource)
                            .anyMatch(attacker -> {
                                var monitoredPositions = board.getImpacts(attacker, Impact.Type.MONITOR).stream()
                                        .map(impact -> (PieceMonitorImpact<?,?>) impact)
                                        .map(PieceMonitorImpact::getPosition)
                                        .toList();

                                return monitoredPositions.contains(targetPosition);
                            });

                    if (isBlocked) {
                        positions.add(targetPosition);
                        break;
                    }
                }
            }
        }

        return !positions.isEmpty();
    }
}