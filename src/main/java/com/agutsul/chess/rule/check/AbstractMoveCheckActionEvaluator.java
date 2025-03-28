package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.ActionFilter;
import com.agutsul.chess.activity.action.PieceBigMoveAction;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

abstract class AbstractMoveCheckActionEvaluator
        implements CheckActionEvaluator {

    protected final Board board;
    protected final Collection<Action<?>> pieceActions;

    AbstractMoveCheckActionEvaluator(Board board,
                                     Collection<Action<?>> pieceActions) {
        this.board = board;
        this.pieceActions = pieceActions;
    }

    @Override
    public Collection<Action<?>> evaluate(KingPiece<?> king) {
        var attackers = this.board.getAttackers(king);

        Collection<PieceCaptureAction<?,?,?,?>> checkActions = attackers.stream()
                .map(attacker -> board.getActions(attacker, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .filter(Action::isCapture)
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(king, action.getTarget()))
                .collect(toSet());

        var filteredActions = new ArrayList<>();

        var moveActionFilter = new ActionFilter<>(PieceMoveAction.class);
        filteredActions.addAll(moveActionFilter.apply(this.pieceActions));

        var hasBigMove = this.pieceActions.stream().anyMatch(Action::isBigMove);
        if (hasBigMove) {
            var bigMoveActionFilter = new ActionFilter<>(PieceBigMoveAction.class);
            filteredActions.addAll(bigMoveActionFilter.apply(this.pieceActions));
        }

        Collection<PieceMoveAction<?,?>> pieceMoveActions = filteredActions.stream()
                .map(action -> (PieceMoveAction<?,?>) action)
                .collect(toList());

        return process(king, checkActions, pieceMoveActions);
    }

    abstract Collection<Action<?>> process(KingPiece<?> king,
                                           Collection<PieceCaptureAction<?,?,?,?>> checkActions,
                                           Collection<PieceMoveAction<?,?>> moveActions);
}