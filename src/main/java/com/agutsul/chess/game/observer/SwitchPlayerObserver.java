package com.agutsul.chess.game.observer;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.event.AbstractEventObserver;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.event.SwitchPlayerEvent;
import com.agutsul.chess.player.Player;

public class SwitchPlayerObserver
        extends AbstractEventObserver<SwitchPlayerEvent> {

    private static final Logger LOGGER = getLogger(SwitchPlayerObserver.class);

    private static final String PLAYER_FORMAT = "%s(%s)";

    private final Game game;

    public SwitchPlayerObserver(Game game) {
        this.game = game;
    }

    @Override
    protected void process(SwitchPlayerEvent event) {
        LOGGER.info("Switched player '{}' => '{}'",
                format(game.getCurrentPlayer()), format(game.getOpponentPlayer())
        );

        game.getOpponentPlayer().disable();
        game.getCurrentPlayer().enable();
    }

    private static String format(Player player) {
        return String.format(PLAYER_FORMAT, player.getName(), player.getColor());
    }
}