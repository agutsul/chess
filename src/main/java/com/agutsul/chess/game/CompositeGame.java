package com.agutsul.chess.game;

import static com.agutsul.chess.game.state.GameState.isUnknown;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.event.GameExceptionEvent;
import com.agutsul.chess.game.event.GameOverEvent;
import com.agutsul.chess.game.event.GameWinnerEvent;
import com.agutsul.chess.rule.winner.WinnerEvaluator;
import com.agutsul.chess.timeout.MixedTimeout;
import com.agutsul.chess.timeout.Timeout;
import com.agutsul.chess.timeout.Timeout.Type;

final class CompositeGame<GAME extends Game & Observable>
        extends AbstractGameProxy<GAME>
        implements Playable {

    private static final Logger LOGGER = getLogger(CompositeGame.class);

    private final Iterator<Timeout> iterator;

    CompositeGame(GAME game, Iterator<Timeout> iterator) {
        super(game);
        this.iterator = iterator;
    }

    @Override
    public void run() {
        // proxy game when iterator is empty ( without any timeout at all )
        if (!this.iterator.hasNext()) {
            LOGGER.info("Proxy game execution");
            super.run();
            return;
        }

        try {
            for (int actionsCounter = 0; this.iterator.hasNext();) {
                var timeout = this.iterator.next();

                LOGGER.info("Game iteration with timeout '{}' started", timeout);

                var playableGame = createIterativeGame(timeout, actionsCounter);
                playableGame.run();

                var isGameFinished = !isUnknown(playableGame.getState());
                if (isGameFinished) {
                    LOGGER.info("Game iteration with timeout '{}' stopped", timeout);
                    return;
                }

                if (timeout.isType(Type.ACTIONS_PER_PERIOD)) {
                    actionsCounter += ((MixedTimeout) timeout).getActionsCounter();
                }

                LOGGER.info("Game iteration with timeout '{}' finished", timeout);
            }

            notifyObservers(new GameWinnerEvent(this.game, WinnerEvaluator.Type.STANDARD));
            notifyObservers(new GameOverEvent(this.game));

            LOGGER.info("Game over ( composite )");
        } catch (Throwable throwable) {
            LOGGER.error("{}: Game exception, board state '{}': {}",
                    getCurrentPlayer().getColor(), getBoard().getState(),
                    getStackTrace(throwable)
            );

            notifyObservers(new GameExceptionEvent(this.game, throwable));
            notifyObservers(new GameOverEvent(this.game));
        }
    }

    private GAME createIterativeGame(Timeout timeout, int actionsCounter) {
        var context = new IterativeGameContext(getContext(), actionsCounter);
        context.setTimeout(timeout.isType(Type.UNKNOWN) ? null : timeout);

        var iGame = new IterativeGame<>(this.game, context);

        @SuppressWarnings("unchecked")
        var playableGame = Stream.of(context.getGameTimeout())
                .flatMap(Optional::stream)
                .map(timeoutMillis -> new IterativeTimeoutGame<>(iGame, timeoutMillis))
                .map(timeoutGame -> (GAME) timeoutGame)
                .findFirst()
                .orElse((GAME) iGame);

        return playableGame;
    }

    private static final class IterativeGame<GAME extends Game & Observable>
            extends AbstractGameProxy<GAME>
            implements Playable {

        private final GameContext context;

        IterativeGame(GAME game, GameContext context) {
            super(game);
            this.context = context;
        }

        @Override
        public GameContext getContext() {
            return this.context;
        }
    }

    private static final class IterativeTimeoutGame<GAME extends Game & Observable>
            extends TimeoutGame<GAME> {

        IterativeTimeoutGame(GAME game, long timeoutMillis) {
            super(game, timeoutMillis);
        }

        @Override
        void evaluateWinner() {
            // prevent winner evaluation on specific iteration and apply it after the last iteration
        }
    }

    private static final class IterativeGameContext
            extends GameContext {

        private final int actionsCounter;

        IterativeGameContext(GameContext context, int actionsCounter) {
            super(context);
            this.actionsCounter = actionsCounter;
        }

        @Override
        public Optional<Integer> getExpectedActions() {
            return Stream.of(super.getExpectedActions())
                    .flatMap(Optional::stream)
                    .map(totalActions -> totalActions + this.actionsCounter)
                    .findFirst();
        }
    }
}