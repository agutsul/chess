package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class AttackerPinCheckActionEvaluator<COLOR extends Color,
                                            KING extends KingPiece<COLOR>>
        implements CheckActionEvalutor<COLOR, KING> {

    private final Board board;
    private final Collection<Action<?>> pieceActions;

    AttackerPinCheckActionEvaluator(Board board,
                                    Collection<Action<?>> pieceActions) {
        this.board = board;
        this.pieceActions = pieceActions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Action<?>> evaluate(KING king) {
        var attackers = board.getAttackers((Piece<Color>) king);
        var checkActions = attackers.stream()
                .map(attacker -> board.getActions(attacker, PieceCaptureAction.class))
                .flatMap(Collection::stream)
                .filter(action -> Objects.equals(king, action.getTarget()))
                .filter(action -> !action.getAttackLine().isEmpty())
                .collect(toSet());

        var pieceMoveActions = board.filterActions(pieceActions, PieceMoveAction.class);

        var actions = new HashSet<Action<?>>();
        for (var checkedAction : checkActions) {
            var attackLine = checkedAction.getAttackLine();

            for (var pieceMoveAction : pieceMoveActions) {
                if (attackLine.contains(pieceMoveAction.getPosition())) {
                    actions.add(pieceMoveAction);
                }
            }
        }

        return actions;
    }
}