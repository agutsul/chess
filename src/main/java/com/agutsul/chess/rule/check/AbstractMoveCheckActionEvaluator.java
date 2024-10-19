package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.function.ActionFilter;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

abstract class AbstractMoveCheckActionEvaluator<COLOR extends Color,
                                                KING extends KingPiece<COLOR>>
        implements CheckActionEvaluator<COLOR, KING> {

    protected final Board board;
    protected final Collection<Action<?>> pieceActions;

    AbstractMoveCheckActionEvaluator(Board board,
                                     Collection<Action<?>> pieceActions) {
        this.board = board;
        this.pieceActions = pieceActions;
    }

    @Override
    public Collection<Action<?>> evaluate(KING king) {
        @SuppressWarnings("unchecked")
        var attackers = board.getAttackers((Piece<Color>) king);

        Collection<PieceCaptureAction<?,?,?,?>> checkActions = attackers.stream()
                .map(attacker -> board.getActions(attacker, PieceCaptureAction.class))
                .flatMap(Collection::stream)
                .filter(action -> Action.Type.CAPTURE.equals(action.getType()))
                .filter(action -> Objects.equals(king, action.getTarget()))
                .filter(action -> !action.getAttackLine().isEmpty())
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .collect(toSet());

        var actionFilter = new ActionFilter<>(PieceMoveAction.class);
        var actions = actionFilter.apply(this.pieceActions);

        Collection<PieceMoveAction<?,?>> pieceMoveActions = actions.stream()
                .map(action -> (PieceMoveAction<?,?>) action)
                .collect(toList());

        return process(king, checkActions, pieceMoveActions);
    }

    abstract Collection<Action<?>> process(KING king,
                                           Collection<PieceCaptureAction<?,?,?,?>> checkActions,
                                           Collection<PieceMoveAction<?,?>> moveActions);
}