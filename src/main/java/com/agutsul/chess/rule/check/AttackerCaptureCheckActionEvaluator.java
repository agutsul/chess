package com.agutsul.chess.rule.check;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isEnPassant;
import static com.agutsul.chess.activity.action.Action.isPromote;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;

import org.apache.commons.collections.map.MultiValueMap;

import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

final class AttackerCaptureCheckActionEvaluator
        implements CheckActionEvaluator {

    private final Board board;
    private final Collection<Action<?>> pieceActions;

    AttackerCaptureCheckActionEvaluator(Board board,
                                        Collection<Action<?>> pieceActions) {
        this.board = board;
        this.pieceActions = pieceActions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> king) {
        var actionTargets = new MultiValueMap();
        for (var action : this.pieceActions) {
            if (isCapture(action) || isEnPassant(action)) {
                var captureAction = (AbstractCaptureAction<?,?,?,?>) action;
                actionTargets.put(captureAction.getTarget(), action);
            } else if (isPromote(action) && isCapture((Action<?>) action.getSource())) {
                var captureAction = (AbstractCaptureAction<?,?,?,?>) action.getSource();
                actionTargets.put(captureAction.getTarget(), action);
            }
        }

        var attackers = board.getAttackers(king);
        var actions = (Collection<Action<?>>) attackers.stream()
                .filter(attacker -> actionTargets.containsKey(attacker))
                .map(attacker -> actionTargets.getCollection(attacker))
                .flatMap(Collection::stream)
                .map(action -> (Action<?>) action)
                .collect(toSet());

        return actions;
    }
}