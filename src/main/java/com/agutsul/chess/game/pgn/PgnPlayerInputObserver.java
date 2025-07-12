package com.agutsul.chess.game.pgn;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.agutsul.chess.antlr.pgn.action.PawnPromotionTypeAdapter;
import com.agutsul.chess.antlr.pgn.action.PgnActionAdapter;
import com.agutsul.chess.antlr.pgn.action.PieceActionAdapter;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.exception.ActionTimeoutException;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.PlayerCommand;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

final class PgnPlayerInputObserver
        extends AbstractPlayerInputObserver {

    private final ActionIterator actionIterator;

    private final PgnActionAdapter pieceActionAdapter;
    private final PgnActionAdapter promotionTypeAdapter;

    PgnPlayerInputObserver(Player player, PgnGame<?> game, List<String> actions) {
        super(player, game);

        this.actionIterator = new ActionIterator(actions);
        this.pieceActionAdapter = new PieceActionAdapter(game.getBoard(), player.getColor());
        this.promotionTypeAdapter = new PawnPromotionTypeAdapter(game.getBoard(), player.getColor());
    }

    @Override
    protected String getActionCommand(Optional<Long> timeout) {
        if (!actionIterator.hasNext()) {
            return finalCommand();
        }

        var action = actionIterator.next();
        return pieceActionAdapter.adapt(action);
    }

    @Override
    protected String getPromotionPieceType(Optional<Long> timeout) {
        var action = actionIterator.current();
        return promotionTypeAdapter.adapt(action);
    }

    private String finalCommand() {
        var pgnGame = (PgnGame<?>) this.game;

        if (PgnTermination.TIME_FORFEIT.equals(pgnGame.getParsedTermination())) {
            throw new ActionTimeoutException(String.format(
                    "%s: '%s' entering action timeout",
                    this.player.getColor(), this.player
            ));
        }

        var gameState = pgnGame.getParsedGameState();
        switch (gameState.getType()) {
        case WHITE_WIN:
            return finalCommand(this.player, Colors.WHITE);
        case BLACK_WIN:
            return finalCommand(this.player, Colors.BLACK);
        case DRAWN_GAME:
            return PlayerCommand.DRAW.code();
        default:
            return PlayerCommand.EXIT.code();
        }
    }

    private static String finalCommand(Player player, Color color) {
        return Objects.equals(player.getColor(), color)
                ? PlayerCommand.WIN.code()
                : PlayerCommand.DEFEAT.code();
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