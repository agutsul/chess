package com.agutsul.chess.rule.checkmate;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

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

        var attackerColor = king.getColor().invert();

        var attackedPiece = Stream.of(king.getAttacked())
                .flatMap(Collection::parallelStream)
                .filter(piece -> !((Protectable) piece).isProtected())
                .filter(piece -> !board.isAttacked(piece.getPosition(), attackerColor))
                .filter(piece -> !board.isMonitored(piece.getPosition(), attackerColor))
                .findFirst();

        return !attackedPiece.isEmpty();
    }
}