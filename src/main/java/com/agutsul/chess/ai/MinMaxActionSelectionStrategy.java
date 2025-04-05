package com.agutsul.chess.ai;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;

// https://en.wikipedia.org/wiki/Minimax
public final class MinMaxActionSelectionStrategy
        extends AbstractActionSelectionStrategy {

    private static final Logger LOGGER = getLogger(MinMaxActionSelectionStrategy.class);

    private static final int DEFAULT_DEPTH = 2;

    public MinMaxActionSelectionStrategy(Game game) {
        this(game, DEFAULT_DEPTH);
    }

    public MinMaxActionSelectionStrategy(Game game, int limit) {
        super(LOGGER, game, limit);
    }

    @Override
    protected AbstractActionSelectionTask createActionSelectionTask(Color color) {
        return new MinMaxActionSelectionTask(
                this.game.getBoard(), this.game.getJournal(), color, this.limit
        );
    }
}