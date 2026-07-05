package com.agutsul.chess.position.cache;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.agutsul.chess.Valuable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.GameInterruptionException;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.ValuablePosition;

public final class PositionCacheImpl<VP extends Position & Valuable<Integer>>
        implements PositionCache<VP> {

    private static final Logger LOGGER = getLogger(PositionCacheImpl.class);

    private final Board board;
    private final Map<String,VP> cache;

    public PositionCacheImpl(Board board) {
        this.board = board;
        this.cache = new HashMap<>();
    }

    @Override
    public void refresh() {
        var tasks = new ArrayList<PositionEvaluationTask<VP>>();
        for (var color : Colors.values()) {
            for (var x = Position.MIN; x < Position.MAX; x++) {
                for (var y = Position.MIN; y < Position.MAX; y++) {
                    tasks.add(createTask(color, positionOf(x, y)));
                }
            }
        }

        try {
            var map = new HashMap<String,VP>();
            for (var future : board.getExecutorService().invokeAll(tasks)) {
                var result = future.get();
                map.put(result.getKey(), result.getValue());
            }

            this.cache.clear();
            this.cache.putAll(map);

        } catch (InterruptedException e) {
            throw new GameInterruptionException("Refreshing board position cache interrupted");
        } catch (ExecutionException e) {
            LOGGER.error("Refreshing board position cache failed", e);
        }
    }

    @Override
    public VP get(Color color, Position position) {
        return this.cache.get(createKey(color, position));
    }

    private PositionEvaluationTask<VP> createTask(Color color, Position position) {
        return new PositionEvaluationTask<>(board, createKey(color, position), color, position);
    }

    private static String createKey(Color color, Position position) {
        return String.format("%s_%s", color, position.getCode());
    }

    private static final class PositionEvaluationTask<PV extends Position & Valuable<Integer>>
            implements Callable<Pair<String,PV>> {

        private final Board board;

        private final String code;
        private final Color color;
        private final Position position;

        PositionEvaluationTask(Board board, String code, Color color, Position position) {
            this.board = board;
            this.code = code;
            this.color = color;
            this.position = position;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Pair<String,PV> call() throws Exception {
            var value = calculateValue(board.getImpacts(color, position));
            return Pair.of(code, (PV) new ValuablePosition<Integer>(position, value));
        }

        private static int calculateValue(Collection<Impact<?>> impacts) {
            return Stream.of(impacts)
                    .flatMap(Collection::parallelStream)
                    .mapToInt(Impact::getValue)
                    .sum();
        }
    }
}