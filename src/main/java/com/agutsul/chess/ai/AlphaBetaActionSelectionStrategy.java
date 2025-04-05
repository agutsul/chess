package com.agutsul.chess.ai;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.Game;

// https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
public final class AlphaBetaActionSelectionStrategy
        extends AbstractActionSelectionStrategy {

    private static final Logger LOGGER = getLogger(AlphaBetaActionSelectionStrategy.class);

    private static final int DEFAULT_DEPTH = 3;

    public AlphaBetaActionSelectionStrategy(Game game) {
        this(game, DEFAULT_DEPTH);
    }

    public AlphaBetaActionSelectionStrategy(Game game, int limit) {
        super(LOGGER, game, limit);
    }

    @Override
    protected AbstractActionSelectionTask createActionSelectionTask(Color color) {
        return new AlphaBetaActionSelectionTask(
                this.game.getBoard(), this.game.getJournal(), color, this.limit
        );
    }
}