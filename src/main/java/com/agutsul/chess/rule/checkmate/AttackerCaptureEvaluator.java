package com.agutsul.chess.rule.checkmate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class AttackerCaptureEvaluator<COLOR extends Color,
                                     KING extends KingPiece<COLOR>>
        implements CheckMateEvaluator<COLOR, KING> {

    private final Board board;

    AttackerCaptureEvaluator(Board board) {
        this.board = board;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Boolean evaluate(KING king) {
        var attackers = board.getAttackers((Piece<Color>) king);
        for (var attacker : attackers) {
            // find any action to capture piece making check
            var captureActions = getAttackActions(attacker);

            // if there is such action
            var isCapturable = !captureActions.isEmpty();
            for (var captureAction : captureActions) {
                var sourcePiece = captureAction.getSource();

                // in case if king is the piece that can attack check maker
                // then king should not be attacked in result of capturing.
                // it can happen when piece making check is protected
                // by some other piece of the same color
                if (Piece.Type.KING.equals(sourcePiece.getType())) {
                    if (board.isProtected(attacker)) {
                        isCapturable = false;
                        break;
                    }
                }
            }

            if (!isCapturable) {
                // it means that there is at least one check maker piece that can't be captured
                return false;
            }
        }

        // all check making pieces can be captured
        return true;
    }

    private Collection<PieceCaptureAction<?,?,?,?>> getAttackActions(Piece<Color> piece) {
        var actions = new ArrayList<PieceCaptureAction<?,?,?,?>>();
        for (var attacker : board.getAttackers(piece)) {
            for (var action : board.getActions(attacker)) {
                var actionType = action.getType();

                if (Action.Type.CAPTURE.equals(actionType)
                        || Action.Type.EN_PASSANT.equals(actionType)) {

                    var captureAction = (PieceCaptureAction<?,?,?,?>) action;
                    if (Objects.equals(captureAction.getTarget(), piece)) {
                        actions.add(captureAction);
                    }
                }

                if (Action.Type.PROMOTE.equals(actionType)) {
                    var sourceAction = ((PiecePromoteAction<?,?>) action).getSource();

                    if (Action.Type.CAPTURE.equals(sourceAction.getType())) {

                        var captureAction = (PieceCaptureAction<?,?,?,?>) sourceAction;
                        if (Objects.equals(captureAction.getTarget(), piece)) {
                            actions.add(captureAction);
                        }
                    }
                }
            }
        }

        return actions;
    }
}