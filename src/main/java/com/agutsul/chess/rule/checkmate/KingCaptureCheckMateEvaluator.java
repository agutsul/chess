package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashSet;

import org.slf4j.Logger;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCaptureAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.position.Position;

final class KingCaptureCheckMateEvaluator
        implements CheckMateEvaluator {

    private static final Logger LOGGER = getLogger(KingCaptureCheckMateEvaluator.class);

    private final Board board;

    KingCaptureCheckMateEvaluator(Board board) {
        this.board = board;
    }

    @Override
    public Boolean evaluate(KingPiece<?> king) {
        LOGGER.info("Evaluate king '{}' capture ability", king);

        var checkMakers = board.getAttackers(king);

        // skip capturing check maker pieces (see AttackerCaptureCheckMateEvaluator)
        var captureActions = board.getActions(king, Action.Type.CAPTURE).stream()
                .map(action -> (PieceCaptureAction<?,?,?,?>) action)
                .filter(action -> !checkMakers.contains(action.getTarget()))
                .toList();

        var positions = new HashSet<Position>();
        for (var action : captureActions) {
            var piece = action.getTarget();

            var isProtected = ((Protectable) piece).isProtected();
            if (!isProtected) {
                positions.add(piece.getPosition());
            }
        }

        return !positions.isEmpty();
    }
}