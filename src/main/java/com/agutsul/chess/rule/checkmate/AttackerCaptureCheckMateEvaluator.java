package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.AbstractCaptureAction;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceEnPassantAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class AttackerCaptureCheckMateEvaluator<COLOR extends Color,
                                              KING extends KingPiece<COLOR>>
        implements CheckMateEvaluator<COLOR, KING> {

    private static final Logger LOGGER = getLogger(AttackerCaptureCheckMateEvaluator.class);

    private final Board board;

    AttackerCaptureCheckMateEvaluator(Board board) {
        this.board = board;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Boolean evaluate(KING king) {
        LOGGER.info("Evaluate attacker capture by king '{}'", king);

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
                if (Piece.Type.KING.equals(sourcePiece.getType())
                        && board.isProtected(attacker)) {

                    isCapturable = false;
                    break;
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

    private Collection<AbstractCaptureAction<?,?,?,?>> getAttackActions(Piece<Color> piece) {
        var attackActions = new ArrayList<AbstractCaptureAction<?,?,?,?>>();
        for (var attacker : board.getAttackers(piece)) {

            var actions = new HashSet<>();
            actions.addAll(board.getActions(attacker, PieceCaptureAction.class));
            actions.addAll(board.getActions(attacker, PieceEnPassantAction.class));

            for (var action : actions) {
                var captureAction = (AbstractCaptureAction<?,?,?,?>) action;
                @SuppressWarnings("unchecked")
                var targetPiece = (Piece<Color>) captureAction.getTarget();
                if (Objects.equals(targetPiece, piece)) {
                    attackActions.add(captureAction);
                }
            }
        }

        return attackActions;
    }
}