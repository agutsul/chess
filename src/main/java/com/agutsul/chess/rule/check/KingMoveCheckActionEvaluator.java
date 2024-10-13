package com.agutsul.chess.rule.check;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.action.function.ActionFilter;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class KingMoveCheckActionEvaluator<COLOR extends Color,
                                         KING extends KingPiece<COLOR>>
        implements CheckActionEvalutor<COLOR, KING> {

    private final Board board;
    private final Collection<Action<?>> pieceActions;

    KingMoveCheckActionEvaluator(Board board,
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

        var actionFilter = new ActionFilter<>(PieceMoveAction.class);
        var pieceMoveActions = actionFilter.apply(this.pieceActions);

        var attackerColor = king.getColor().invert();

        var actions = new HashSet<Action<?>>();
        for (var checkedAction : checkActions) {
            var attackLine = checkedAction.getAttackLine();

            for (var pieceMoveAction : pieceMoveActions) {
                var targetPosition = pieceMoveAction.getPosition();
                if (attackLine.contains(targetPosition)) {
                    continue;
                }

                if (!board.isAttacked(targetPosition, attackerColor)
                        && !board.isMonitored(targetPosition, attackerColor)) {

                    actions.add(pieceMoveAction);
                }
            }
        }

        return actions;
    }
}
