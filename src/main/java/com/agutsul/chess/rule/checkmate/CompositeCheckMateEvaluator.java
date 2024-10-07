package com.agutsul.chess.rule.checkmate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.KingPiece;

public final class CompositeCheckMateEvaluator<COLOR extends Color,
                                               KING extends KingPiece<COLOR>>
        implements CheckMateEvaluator<COLOR, KING> {

    private final List<CheckMateEvaluator<COLOR, KING>> evaluators;

    public CompositeCheckMateEvaluator(Board board) {
        this.evaluators = List.of(
                new KingMoveEvaluator<>(board),
                new AttackerCaptureEvaluator<>(board),
                new AttackerPinEvaluator<>(board)
            );
    }

    @Override
    public Boolean evaluate(KING king) {
        var results = new ArrayList<Boolean>();
        var executor = Executors.newFixedThreadPool(evaluators.size());
        try {
            var tasks = evaluators.stream()
                    .map(e -> new CheckMateEvaluatorTask<COLOR, KING>(king, e))
                    .toList();

            try {
                for (var future : executor.invokeAll(tasks)) {
                    results.add(future.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                // TODO implement proper logging
                System.err.println(e);
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

        return !results.isEmpty() && !results.contains(true);
    }

    private static class CheckMateEvaluatorTask<COLOR extends Color,
                                                KING extends KingPiece<COLOR>>
            implements Callable<Boolean> {

        private final KING king;
        private final CheckMateEvaluator<COLOR, KING> checkMateEvaluator;

        CheckMateEvaluatorTask(KING king,
                               CheckMateEvaluator<COLOR, KING> checkMateEvaluator) {
            this.king = king;
            this.checkMateEvaluator = checkMateEvaluator;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                return checkMateEvaluator.evaluate(king);
            } catch (Throwable t) {
                // TODO implement proper logging
                System.err.println(t);
            }
            return true;
        }
    }
}