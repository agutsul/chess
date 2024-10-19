package com.agutsul.chess.rule.check;

import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public final class CompositeCheckActionEvaluator<COLOR extends Color,
                                                 KING extends KingPiece<COLOR>>
        implements CheckActionEvaluator<COLOR, KING> {

    private static final Logger LOGGER = getLogger(CompositeCheckActionEvaluator.class);

    private final List<CheckActionEvaluator<COLOR, KING>> evaluators;

    public CompositeCheckActionEvaluator(Board board,
                                         Piece<COLOR> piece,
                                         Collection<Action<?>> actions) {
        this.evaluators = List.of(
                new AttackerCaptureCheckActionEvaluator<>(board, actions),
                Piece.Type.KING.equals(piece.getType())
                    ? new KingMoveCheckActionEvaluator<>(board, actions)
                    : new AttackerPinCheckActionEvaluator<>(board, actions)
        );
    }

    @Override
    public Collection<Action<?>> evaluate(KING king) {
        var executor = newFixedThreadPool(this.evaluators.size());
        try {
            var tasks = this.evaluators.stream()
                    .map(evaluator -> new Callable<Collection<Action<?>>>() {
                        @Override
                        public Collection<Action<?>> call() throws Exception {
                            return evaluator.evaluate(king);
                        }
                    })
                    .toList();

            try {
                var results = new ArrayList<Action<?>>();
                for (var future : executor.invokeAll(tasks)) {
                    results.addAll(future.get());
                }

                return results;
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

        return emptyList();
    }
}