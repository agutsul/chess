package com.agutsul.chess.game.pgn;

import static com.agutsul.chess.game.state.GameState.Type.DRAWN_GAME;
import static org.apache.commons.lang3.ThreadUtils.sleepQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;

import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.pgn.action.PawnPromotionTypeAdapter;
import com.agutsul.chess.pgn.action.PieceActionAdapter;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

final class PgnPlayerInputObserver
        extends AbstractPlayerInputObserver {

    private static final Logger LOGGER = getLogger(PgnPlayerInputObserver.class);

    private final ActionIterator actionIterator;

    private final PieceActionAdapter pieceActionAdapter;
    private final PawnPromotionTypeAdapter promotionTypeAdapter;

    PgnPlayerInputObserver(Player player, PgnGame game, List<String> actions) {
        super(LOGGER, player, game);

        this.actionIterator = new ActionIterator(actions);

        this.pieceActionAdapter = new PieceActionAdapter(game.getBoard(), player.getColor());
        this.promotionTypeAdapter = new PawnPromotionTypeAdapter(game.getBoard(), player.getColor());
    }

    @Override
    protected String getActionCommand() {
        if (!actionIterator.hasNext()) {
            return finalCommand(((PgnGame) this.game).getParsedGameState());
        }

        var action = actionIterator.next();
        var adaptedAction = pieceActionAdapter.adapt(action);

        // simulate a delay
        sleepQuietly(Duration.ofMillis(10));

        // uncomment below for local debug of pgn file
//        System.out.println(String.format("%s: %s: '%s'",
//                player.getColor(), player.getName(), adaptedAction));

        return adaptedAction;
    }

    @Override
    protected String getPromotionPieceType() {
        var action = actionIterator.current();
        return promotionTypeAdapter.adapt(action);
    }

    private static String finalCommand(GameState gameState) {
        var command = DRAWN_GAME.equals(gameState.getType())
                ? DRAW_COMMAND
                : EXIT_COMMAND;

        return command;
    }

    private static final class ActionIterator
            implements Iterator<String> {

        private final Iterator<String> iterator;

        // temporary cache action to be used later for promotion action
        // to resolve user selected promotion piece type
        private String current;

        ActionIterator(List<String> actions) {
            this.iterator = actions.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public String current() {
            return this.current;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                return null;
            }

            var action = this.iterator.next();
            this.current = action;

            return action;
        }
    }
}