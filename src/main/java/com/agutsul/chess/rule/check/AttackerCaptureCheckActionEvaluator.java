package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.ActionFilter;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceEnPassantAction;
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
        var filteredActions = new HashSet<>();

        var captureFilter = new ActionFilter<>(PieceCaptureAction.class);
        filteredActions.addAll(captureFilter.apply(this.pieceActions));

        var hasEnPassante = this.pieceActions.stream().anyMatch(Action::isEnPassant);
        if (hasEnPassante) {
            var enPassantFilter = new ActionFilter<>(PieceEnPassantAction.class);
            filteredActions.addAll(enPassantFilter.apply(this.pieceActions));
        }

        var attackers = board.getAttackers(king);
        Collection<Action<?>> actions = attackers.stream()
                .flatMap(attacker -> filteredActions.stream()
                        .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                        .filter(action -> Objects.equals(action.getTarget(), attacker))
                )
                .collect(toSet());

        return actions;
    }
}