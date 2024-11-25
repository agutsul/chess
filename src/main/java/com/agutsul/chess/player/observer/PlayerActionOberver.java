package com.agutsul.chess.player.observer;

import static org.apache.commons.lang3.ThreadUtils.sleepQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.command.CancelActionCommand;
import com.agutsul.chess.command.DrawGameCommand;
import com.agutsul.chess.command.ExitGameCommand;
import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionEvent;
import com.agutsul.chess.player.event.PlayerDrawActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerExitActionEvent;
import com.agutsul.chess.player.event.PlayerExitActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

public final class PlayerActionOberver
        implements Observer {

    private static final Logger LOGGER = getLogger(PlayerActionOberver.class);

    private final Game game;

    public PlayerActionOberver(Game game) {
        this.game = game;
    }

    @Override
    public void observe(Event event) {
        if (event instanceof PlayerActionEvent) {
            process((PlayerActionEvent) event);
        } else if (event instanceof PlayerCancelActionEvent) {
            process((PlayerCancelActionEvent) event);
        } else if (event instanceof PlayerDrawActionEvent) {
            process((PlayerDrawActionEvent) event);
        } else if (event instanceof PlayerExitActionEvent) {
            process((PlayerExitActionEvent) event);
        }
    }

    private void process(PlayerActionEvent event) {
        var board = ((AbstractPlayableGame) this.game).getBoard();
        try {
            var command = new PerformActionCommand(event.getPlayer(), board, (Observable) this.game);
            command.setSource(event.getSource());
            command.setTarget(event.getTarget());

            command.execute();
        } catch (Exception e) {
            LOGGER.error("Player action exception", e);
            notifyGameEvent(new PlayerActionExceptionEvent(e.getMessage()));

            requestPlayerAction(board, event.getPlayer());
        }
    }

    private void process(PlayerCancelActionEvent event) {
        var player = event.getPlayer();
        var playerColor = player.getColor();

        try {
            // cancel opponent player action
            var cancelOpponentActionCommand = new CancelActionCommand(this.game, playerColor.invert());
            cancelOpponentActionCommand.execute();

            // cancel current player action
            var cancelActionCommand = new CancelActionCommand(this.game, playerColor);
            cancelActionCommand.execute();
        } catch (Exception e) {
            LOGGER.error("Player cancel action exception", e);
            notifyGameEvent(new PlayerCancelActionExceptionEvent(e.getMessage()));
        } finally {
            requestPlayerAction(player);
        }
    }

    private void process(PlayerDrawActionEvent event) {
        var player = event.getPlayer();
        try {
            var drawGameCommand = new DrawGameCommand(this.game, player);
            drawGameCommand.execute();
        } catch (Exception e) {
            LOGGER.error("Player draw exception", e);
            notifyGameEvent(new PlayerDrawActionExceptionEvent(e.getMessage()));

            requestPlayerAction(player);
        }
    }

    private void process(PlayerExitActionEvent event) {
        var player = event.getPlayer();
        try {
            var exitGameCommand = new ExitGameCommand(this.game, player);
            exitGameCommand.execute();
        } catch (Exception e) {
            LOGGER.error("Player exit exception", e);
            notifyGameEvent(new PlayerExitActionExceptionEvent(e.getMessage()));

            requestPlayerAction(player);
        }
    }

    private void notifyGameEvent(Event event) {
        // display error message to player
        ((Observable) this.game).notifyObservers(event);
        sleepQuietly(Duration.ofMillis(1));
    }

    private void requestPlayerAction(Player player) {
        var board = ((AbstractPlayableGame) this.game).getBoard();
        requestPlayerAction(board, player);
    }

    private static void requestPlayerAction(Board board, Player player) {
        // re-ask player about new action
        ((Observable) board).notifyObservers(new RequestPlayerActionEvent(player));
    }
}