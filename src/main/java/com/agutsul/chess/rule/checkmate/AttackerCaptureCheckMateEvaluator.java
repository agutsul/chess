package com.agutsul.chess.rule.checkmate;

import static java.util.function.Predicate.not;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.action.AbstractCaptureAction;
import com.agutsul.chess.activity.action.Action;
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

        var isCapturable = Stream.of(board.getAttackers(king))
                .flatMap(Collection::stream)
                .anyMatch(checkMaker -> Stream.of(board.getAttackers(checkMaker))
                        .flatMap(Collection::stream)
                        .filter(not(Piece::isKing))
                        .filter(attacker -> !((Pinnable) attacker).isPinned())
                        .map(attacker -> board.getActions(attacker, Action.Type.CAPTURE))
                        .flatMap(Collection::stream)
                        .map(action -> (AbstractCaptureAction<?,?,?,?>) action)
                        .map(AbstractCaptureAction::getTarget)
                        .anyMatch(targetPiece -> Objects.equals(targetPiece, checkMaker))
                );

        return isCapturable;
    }
}