package com.agutsul.chess.rule.checkmate;

import static java.util.function.Predicate.not;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Attackable;
import com.agutsul.chess.Pinnable;
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
        LOGGER.info("Evaluate checkmaker capture by any piece except king '{}'", king);

        var isCaptured = Stream.of(king.getAttackers())
                .flatMap(Collection::parallelStream)
                .anyMatch(checkMaker -> Stream.of(((Attackable) checkMaker).getAttackers())
                        .flatMap(Collection::parallelStream)
                        .filter(not(Piece::isKing))
                        .filter(checkMakeAttacker -> !((Pinnable) checkMakeAttacker).isPinned())
                        .map(checkMakeAttacker -> (Attackable) checkMakeAttacker)
                        .map(Attackable::getAttacked)
                        .flatMap(Collection::parallelStream)
                        .anyMatch(attacked -> Objects.equals(attacked, checkMaker))
                );

        return isCaptured;
    }
}