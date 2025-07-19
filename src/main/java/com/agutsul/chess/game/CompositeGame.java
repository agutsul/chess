package com.agutsul.chess.game;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.event.Observable;
import com.agutsul.chess.game.observer.GameTimeoutTerminationObserver;
import com.agutsul.chess.timeout.MixedTimeout;
import com.agutsul.chess.timeout.Timeout;
import com.agutsul.chess.timeout.Timeout.Type;

final class CompositeGame<GAME extends Game & Observable>
        extends AbstractGameProxy<GAME>
        implements Playable {

    private final Iterator<Timeout> iterator;

    CompositeGame(GAME game, Iterator<Timeout> iterator) {
        super(game);
        this.iterator = iterator;

        addObserver(new GameTimeoutTerminationObserver());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        // proxy game when iterator is empty ( without any timeout at all )
        if (!this.iterator.hasNext()) {
            this.game.run();
            return;
        }

        // iterate over specified timeouts
        for (int actionsCounter = 0; this.iterator.hasNext();) {
            var timeout = this.iterator.next();

            var context = new IterativeGameContext(getContext(), actionsCounter);
            context.setTimeout(timeout.isType(Type.UNKNOWN) ? null : timeout);

            var iGame = new IterativeGame<>(this.game, context);
            var playableGame = Stream.of(context.getGameTimeout())
                    .flatMap(Optional::stream)
                    .map(timeoutMillis -> new TimeoutGame<>(iGame, timeoutMillis))
                    .map(timeoutGame -> (GAME) timeoutGame)
                    .findFirst()
                    .orElse((GAME) iGame);

            playableGame.run();

            var boardState = getBoard().getState();
            if (boardState.isTerminal()) {
                break;
            }

            if (timeout.isType(Type.ACTIONS_PER_PERIOD)) {
                actionsCounter += ((MixedTimeout) timeout).getActionsCounter();
            }
        }
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

    private static final class IterativeGameContext
            extends GameContext {

        private final int actionsCounter;

        IterativeGameContext(GameContext context, int actionsCounter) {
            super(context);
            this.actionsCounter = actionsCounter;
        }

        @Override
        public Optional<Integer> getTotalActions() {
            return Stream.of(super.getTotalActions())
                    .flatMap(Optional::stream)
                    .map(totalActions -> totalActions + this.actionsCounter)
                    .findFirst();
        }
    }
}