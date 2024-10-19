package com.agutsul.chess.rule.check;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.AbstractCaptureAction;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.action.function.ActionFilter;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class AttackerCaptureCheckActionEvaluator<COLOR extends Color,
                                                KING extends KingPiece<COLOR>>
        implements CheckActionEvaluator<COLOR, KING> {

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
        var attackers = board.getAttackers((Piece<Color>) king);

        var filteredActions = new HashSet<>();

        var captureFilter = new ActionFilter<>(PieceCaptureAction.class);
        filteredActions.addAll(captureFilter.apply(this.pieceActions));

        var enPassantFilter = new ActionFilter<>(PieceEnPassantAction.class);
        filteredActions.addAll(enPassantFilter.apply(this.pieceActions));

        var actions = new HashSet<Action<?>>();
        for (var attacker : attackers) {
            for (var action : filteredActions) {
                var captureAction = (AbstractCaptureAction<?,?,?,?>) action;

                var targetPiece = (Piece<Color>) captureAction.getTarget();
                if (Objects.equals(targetPiece, attacker)) {
                    actions.add(captureAction);
                }
            }
        }

        return actions;
    }
}