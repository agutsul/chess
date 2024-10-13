package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.function.ActionFilter;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class AttackerCaptureCheckActionEvaluator<COLOR extends Color,
                                                KING extends KingPiece<COLOR>>
        implements CheckActionEvalutor<COLOR, KING> {

    private final Board board;

    private final Collection<Action<?>> pieceActions;

    AttackerCaptureCheckActionEvaluator(Board board,
                                        Collection<Action<?>> pieceActions) {
        this.board = board;
        this.pieceActions = pieceActions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Action<?>> evaluate(KING king) {
        var actionFilter = new ActionFilter<>(PieceCaptureAction.class);
        var pieceCaptureActions = actionFilter.apply(this.pieceActions);

        var attackers = board.getAttackers((Piece<Color>) king);

        var actions = new HashSet<Action<?>>();
        for (var attacker : attackers) {
            for (var pieceCaptureAction : pieceCaptureActions) {
                var targetPiece = (Piece<Color>) pieceCaptureAction.getTarget();
                if (Objects.equals(targetPiece, attacker)) {
                    actions.add(pieceCaptureAction);
                }
            }
        }

        return actions;
    }
}