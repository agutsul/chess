package com.agutsul.chess.board.state;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.cache.ActivityCache;
import com.agutsul.chess.activity.cache.ActivityCacheImpl;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.Rule;

abstract class AbstractPlayableBoardState
        extends AbstractBoardState
        implements PlayableBoardState {

    private final Rule<Position,Collection<Impact<?>>> impactRule;
    private final Map<Position,ActivityCache<Impact.Type,Impact<?>>> impactCache;

    AbstractPlayableBoardState(Logger logger, Type type, Board board, Color color,
                               Rule<Position,Collection<Impact<?>>> impactRule) {

        super(logger, type, board, color);

        this.impactRule  = impactRule;
        this.impactCache = new HashMap<>();
    }

    @Override
    public Collection<Action<?>> getActions(Piece<?> piece) {
        logger.info("Getting actions for piece '{}'", piece);
        return piece.getActions();
    }

    @Override
    public Collection<Impact<?>> getImpacts(Position position) {
        logger.info("Getting impacts for color '{}' and position '{}'",
                getColor(), position
        );

        if (impactCache.containsKey(position)) {
            return Stream.ofNullable(impactCache.get(position))
                .map(ActivityCache::getAll)
                .flatMap(Collection::stream)
                .toList();
        }

        logger.info("Calculate impacts for color '{}' and position '{}'",
                getColor(), position
        );

        var impacts = impactRule.evaluate(position);

        var cache = new ActivityCacheImpl<Impact.Type,Impact<?>>();
        cache.putAll(impacts);

        impactCache.put(position, cache);

        return unmodifiableCollection(impacts);
    }
}