package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.function.ActionFilter;
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
        var attackers = board.getAttackers(king);

        Collection<PieceCaptureAction<?,?,?,?>> checkActions = attackers.stream()
                .map(attacker -> board.getActions(attacker, PieceCaptureAction.class))
                .flatMap(Collection::stream)
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .filter(action -> Objects.equals(king, action.getTarget()))
                .collect(toSet());

        var actionFilter = new ActionFilter<>(PieceMoveAction.class);
        var actions = actionFilter.apply(this.pieceActions);

        Collection<PieceMoveAction<?,?>> pieceMoveActions = actions.stream()
                .map(action -> (PieceMoveAction<?,?>) action)
                .collect(toList());

        return process(king, checkActions, pieceMoveActions);
    }

    abstract Collection<Action<?>> process(KingPiece<?> king,
                                           Collection<PieceCaptureAction<?,?,?,?>> checkActions,
                                           Collection<PieceMoveAction<?,?>> moveActions);
}