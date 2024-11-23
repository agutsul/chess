package com.agutsul.chess.game.pgn;

import static org.apache.commons.lang3.ThreadUtils.sleepQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.pgn.action.PawnPromotionTypeAdapter;
import com.agutsul.chess.pgn.action.PieceActionAdapter;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

final class PgnPlayerInputObserver
        extends AbstractPlayerInputObserver {

    private static final Logger LOGGER = getLogger(PgnPlayerInputObserver.class);

    private final Board board;
    private final ActionIterator actionIterator;

    private final PieceActionAdapter pieceActionAdapter;
    private final PawnPromotionTypeAdapter promotionTypeAdapter;

    PgnPlayerInputObserver(Player player, Game game, List<String> actions) {
        super(LOGGER, player, game);

        this.board = ((AbstractPlayableGame) game).getBoard();
        this.actionIterator = new ActionIterator(actions);

        this.pieceActionAdapter = new PieceActionAdapter(board, player.getColor());
        this.promotionTypeAdapter = new PawnPromotionTypeAdapter(board, player.getColor());
    }

    @Override
    protected String getActionCommand() {
        if (!actionIterator.hasNext()) {
            return DRAW_COMMAND;
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

    private static final class ActionIterator
            implements Iterator<String> {

        private final Iterator<String> actionIterator;

        // temporary cache action to be used later for promotion action
        // to resolve user selected promotion piece type
        private String current;

        ActionIterator(List<String> actions) {
            this.actionIterator = actions.iterator();
        }

        public String current() {
            return this.current;
        }

        @Override
        public boolean hasNext() {
            return this.actionIterator.hasNext();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                return null;
            }

            var action = this.actionIterator.next();
            this.current = action;

            return action;
        }
    }
}