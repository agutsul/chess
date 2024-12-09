package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import com.agutsul.chess.action.AbstractCaptureAction;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.function.ActionFilter;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

final class KingCapturePieceActionEvaluator
        implements CheckActionEvaluator {

    private final Board board;
    private final Collection<Action<?>> pieceActions;

    KingCapturePieceActionEvaluator(Board board,
                                     Collection<Action<?>> pieceActions) {
        this.board = board;
        this.pieceActions = pieceActions;
    }

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> king) {
        var pieces = board.getPieces(king.getColor().invert());

        var filteredActions = new HashSet<>();

        var captureFilter = new ActionFilter<>(PieceCaptureAction.class);
        filteredActions.addAll(captureFilter.apply(this.pieceActions));

        var actions = new HashSet<Action<?>>();
        for (var piece : pieces) {
            for (var action : filteredActions) {
                var captureAction = (AbstractCaptureAction<?,?,?,?>) action;

                if (Objects.equals(captureAction.getTarget(), piece)) {
                    actions.add(captureAction);
                }
            }
        }

        return actions;
    }
}