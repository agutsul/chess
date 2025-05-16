package com.agutsul.chess.rule.check;

import static com.agutsul.chess.activity.action.Action.isBigMove;
import static com.agutsul.chess.activity.action.Action.isMove;
import static com.agutsul.chess.activity.action.Action.isPromote;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
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

        var filteredActions = new ArrayList<Action<?>>();
        for (var action : this.pieceActions) {
            if (isMove(action) || isBigMove(action)) {
                filteredActions.add(action);
            } else if (isPromote(action) && isMove((Action<?>) action.getSource())) {
                filteredActions.add(action);
            }
        }

        return process(king, checkActions, filteredActions);
    }

    abstract Collection<Action<?>> process(KingPiece<?> king,
                                           Collection<PieceCaptureAction<?,?,?,?>> checkActions,
                                           Collection<Action<?>> actions);
}