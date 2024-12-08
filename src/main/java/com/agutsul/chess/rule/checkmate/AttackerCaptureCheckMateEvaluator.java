package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.action.AbstractCaptureAction;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.Protectable;

final class AttackerCaptureCheckMateEvaluator
        implements CheckMateEvaluator {

    private static final Logger LOGGER = getLogger(AttackerCaptureCheckMateEvaluator.class);

    private final Board board;

    AttackerCaptureCheckMateEvaluator(Board board) {
        this.board = board;
    }

    @Override
    public Boolean evaluate(KingPiece<?> king) {
        LOGGER.info("Evaluate attacker capture by king '{}'", king);

        var attackers = board.getAttackers(king);
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
                        && ((Protectable) attacker).isProtected()) {

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

    private Collection<AbstractCaptureAction<?,?,?,?>> getAttackActions(Piece<?> piece) {
        var attackActions = new ArrayList<AbstractCaptureAction<?,?,?,?>>();
        for (var attacker : board.getAttackers(piece)) {
            var actions = board.getActions(attacker, PieceCaptureAction.class);

            attackActions.addAll(actions.stream()
                    .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                    .filter(action -> {
                        var victim = action.getTarget();
                        return Objects.equals(victim.getPosition(), piece.getPosition());
                    })
                    .toList()
            );
        }

        return attackActions;
    }
}