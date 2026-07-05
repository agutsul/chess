package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceMonitorImpact;
import com.agutsul.chess.board.Board;
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
                                  Collection<Action<?>> actions) {

        var attackerColor = king.getColor().invert();

        var availableActions = Stream.of(actions)
                .flatMap(Collection::parallelStream)
                // skip positions attacked by any opponent piece
                .filter(action -> !board.isAttacked(action.getPosition(), attackerColor))
                // skip monitored positions
                // for example positions behind the king but on the same attack line
                .filter(action -> !board.isMonitored(action.getPosition(), attackerColor))
                .toList();

        var filteredActions = Stream.of(checkActions)
                .flatMap(Collection::parallelStream)
                .flatMap(checkAction -> Stream.of(availableActions)
                        .flatMap(Collection::parallelStream)
                        // skip moves on positions inside attack line
                        .filter(action -> checkAction.getLine().stream()
                                    .noneMatch(line -> line.contains(action.getPosition()))
                        )
                        .filter(action -> {
                            // skip positions monitored by check maker.
                            // position can be monitored by some other opponent's piece
                            // but as soon as position not directly attacked by the opponent piece
                            // it is valid for move action
                            var monitoredPositions = Stream.of(board.getImpacts(checkAction.getSource(), Impact.Type.MONITOR))
                                    .flatMap(Collection::parallelStream)
                                    .map(impact -> (PieceMonitorImpact<?,?>) impact)
                                    .map(PieceMonitorImpact::getPosition)
                                    .toList();

                            return !monitoredPositions.contains(action.getPosition());
                        })
                )
                .collect(toSet());

        return filteredActions;
    }
}