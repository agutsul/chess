package com.agutsul.chess.game.pgn;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.antlr.pgn.action.PawnPromotionTypeAdapter;
import com.agutsul.chess.antlr.pgn.action.PgnActionAdapter;
import com.agutsul.chess.antlr.pgn.action.PieceActionAdapter;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

final class PgnPlayerInputObserver
        extends AbstractPlayerInputObserver {

    private static final Logger LOGGER = getLogger(PgnPlayerInputObserver.class);

    private final ActionIterator actionIterator;

    private final PgnActionAdapter pieceActionAdapter;
    private final PgnActionAdapter promotionTypeAdapter;

    PgnPlayerInputObserver(Player player, PgnGame game, List<String> actions) {
        super(LOGGER, player, game);

        this.actionIterator = new ActionIterator(actions);
        this.pieceActionAdapter = new PieceActionAdapter(game.getBoard(), player.getColor());
        this.promotionTypeAdapter = new PawnPromotionTypeAdapter(game.getBoard(), player.getColor());
    }

    @Override
    protected String getActionCommand() {
        if (!actionIterator.hasNext()) {
            return finalCommand();
        }

        var action = actionIterator.next();
        return pieceActionAdapter.adapt(action);
    }

    @Override
    protected String getPromotionPieceType() {
        var action = actionIterator.current();
        return promotionTypeAdapter.adapt(action);
    }

    private String finalCommand() {
        var pgnGame = (PgnGame) this.game;
        var gameState = pgnGame.getParsedGameState();

        switch (gameState.getType()) {
        case WHITE_WIN:
            return getPlayerCommand(Colors.WHITE);
        case BLACK_WIN:
            return getPlayerCommand(Colors.BLACK);
        case DRAWN_GAME:
            return DRAW_COMMAND;
        default:
            return EXIT_COMMAND;
        }
    }

    private String getPlayerCommand(Color color) {
        return Objects.equals(color, player.getColor())
                ? WIN_COMMAND
                : DEFEAT_COMMAND;
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