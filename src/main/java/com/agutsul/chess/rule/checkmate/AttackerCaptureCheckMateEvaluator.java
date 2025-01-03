package com.agutsul.chess.rule.checkmate;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

final class AttackerCaptureCheckMateEvaluator
        implements CheckMateEvaluator {

    private static final Logger LOGGER = getLogger(AttackerCaptureCheckMateEvaluator.class);

    private final Board board;

    AttackerCaptureCheckMateEvaluator(Board board) {
        this.board = board;
    }

    @Override
    public Boolean evaluate(KingPiece<?> king) {
        LOGGER.info("Evaluate attacker capture by any piece except king '{}'", king);

        var checkMakers = board.getAttackers(king);
        var checkMakerStatus = new HashMap<Piece<?>,Boolean>(checkMakers.size());

        // check if there is any action to capture piece making a check
        for (var checkMaker : checkMakers) {
            var isCapturable = isCheckmakerCapturable(checkMaker);
            checkMakerStatus.put(checkMaker, isCapturable);
        }

        // check if it is possible to capture check maker by the king
        var uncapturedCheckMakers = checkMakerStatus.entrySet().stream()
                .filter(entry -> !entry.getValue())
                .map(entry -> entry.getKey())
                .toList();

        LOGGER.info("Evaluate attacker capture by king '{}'", king);

        var kingAttackedPieces = board.getActions(king, Action.Type.CAPTURE).stream()
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .map(PieceCaptureAction::getTarget)
                .collect(toSet());

        for (var checkMaker : uncapturedCheckMakers) {
            var isCapturable = kingAttackedPieces.contains(checkMaker);
            if (isCapturable) {
                var isProtected = ((Protectable) checkMaker).isProtected();
                checkMakerStatus.put(checkMaker, !isProtected);
            } else {
                checkMakerStatus.put(checkMaker, isCapturable);
            }
        }

        var isCapturable = checkMakerStatus.containsValue(true);
        return isCapturable;
    }

    private boolean isCheckmakerCapturable(Piece<?> piece) {
        var attackers = board.getAttackers(piece);

        boolean isCapturable = attackers.stream()
                .filter(attacker -> !Piece.Type.KING.equals(attacker.getType()))
                .map(attacker -> board.getActions(attacker, Action.Type.CAPTURE))
                .flatMap(Collection::stream)
                .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                .anyMatch(action -> Objects.equals(action.getTarget(), piece));

        return isCapturable;
    }
}