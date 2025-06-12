package com.agutsul.chess.rule.check;

import static com.agutsul.chess.activity.action.Action.isCapture;
import static com.agutsul.chess.activity.action.Action.isEnPassant;
import static com.agutsul.chess.activity.action.Action.isPromote;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class AttackerCaptureCheckActionEvaluator
        implements CheckActionEvaluator {

    private final Board board;
    private final Collection<Action<?>> pieceActions;

    AttackerCaptureCheckActionEvaluator(Board board,
                                        Collection<Action<?>> pieceActions) {
        this.board = board;
        this.pieceActions = pieceActions;
    }

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> king) {
        var actionTargets = new ArrayListValuedHashMap<Piece<?>,Action<?>>();
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
        var actions = attackers.stream()
            .filter(attacker -> actionTargets.containsKey(attacker))
            .map(attacker -> actionTargets.get(attacker))
            .flatMap(Collection::stream)
            .collect(toSet());

        return actions;
    }
}