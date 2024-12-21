package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
import com.agutsul.chess.activity.action.function.ActionFilter;
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

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> king) {
        var attackers = board.getAttackers(king);

        var filteredActions = new HashSet<>();

        var captureFilter = new ActionFilter<>(PieceCaptureAction.class);
        filteredActions.addAll(captureFilter.apply(this.pieceActions));

        var hasEnPassante = pieceActions.stream()
                .map(Action::getType)
                .anyMatch(actionType -> Action.Type.EN_PASSANT.equals(actionType));

        if (hasEnPassante) {
            var enPassantFilter = new ActionFilter<>(PieceEnPassantAction.class);
            filteredActions.addAll(enPassantFilter.apply(this.pieceActions));
        }

        var actions = new HashSet<Action<?>>();
        for (var attacker : attackers) {
            for (var action : filteredActions) {
                var captureAction = (AbstractCaptureAction<?,?,?,?>) action;

                if (Objects.equals(captureAction.getTarget(), attacker)) {
                    actions.add(captureAction);
                }
            }
        }

        return actions;
    }
}