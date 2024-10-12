package com.agutsul.chess.rule.check;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public final class CompositeCheckActionEvaluator<COLOR extends Color,
                                                 KING extends KingPiece<COLOR>>
        implements CheckActionEvalutor<COLOR, KING> {

    private static final Logger LOGGER = getLogger(CompositeCheckActionEvaluator.class);

    private final List<CheckActionEvalutor<COLOR, KING>> evaluators;

    public CompositeCheckActionEvaluator(Board board,
                                         Piece.Type pieceType,
                                         Collection<Action<?>> actions) {
        this.evaluators = List.of(
                new AttackerCaptureCheckActionEvaluator<>(board, actions),
                Piece.Type.KING.equals(pieceType)
                    ? new KingMoveCheckActionEvaluator<>(board, actions)
                    : new AttackerPinCheckActionEvaluator<>(board, actions)
        );
    }

    @Override
    public Collection<Action<?>> evaluate(KING king) {
        var results = new ArrayList<Action<?>>();

        var executor = Executors.newFixedThreadPool(evaluators.size());
        try {
            var tasks = evaluators.stream()
                    .map(evaluator -> new Callable<Collection<Action<?>>>() {
                        @Override
                        public Collection<Action<?>> call() throws Exception {
                            return evaluator.evaluate(king);
                        }
                    })
                    .toList();

            try {
                for (var future : executor.invokeAll(tasks)) {
                    results.addAll(future.get());
                }
            } catch (InterruptedException e) {
                LOGGER.error("Check action evaluation interrupted", e);
            } catch (ExecutionException e) {
                LOGGER.error("Check action evaluation failed", e);
            }
        } finally {
            try {
                executor.shutdown();
                if (!executor.awaitTermination(1, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        return results;
    }
}