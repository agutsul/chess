package com.agutsul.chess.player;

import static org.apache.commons.lang3.ThreadUtils.sleepQuietly;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;

import org.slf4j.Logger;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.command.CancelActionCommand;
import com.agutsul.chess.command.PerformActionCommand;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.event.PlayerActionEvent;
import com.agutsul.chess.player.event.PlayerActionExceptionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionEvent;
import com.agutsul.chess.player.event.PlayerCancelActionExceptionEvent;
import com.agutsul.chess.player.event.RequestPlayerActionEvent;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class PlayerEventOberver
        implements Observer {

    private static final Logger LOGGER = getLogger(PlayerEventOberver.class);

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    private final Game game;

    public PlayerEventOberver(Game game) {
        this.game = game;
    }

    @Override
    public void observe(Event event) {
        if (event instanceof PlayerActionEvent) {
            process((PlayerActionEvent) event);
        } else if (event instanceof PlayerCancelActionEvent) {
            process((PlayerCancelActionEvent) event);
        }
    }

    private void process(PlayerActionEvent event) {
        var board = ((AbstractGame) this.game).getBoard();
        try {
            var command = new PerformActionCommand(board, (Observable) this.game);
            command.setSource(event.getSource());
            command.setTarget(event.getTarget());

            command.execute();
        } catch (Exception e) {
            LOGGER.error("Player action exception", e);
            notifyExceptionEvent(new PlayerActionExceptionEvent(e.getMessage()));

            sleepQuietly(Duration.ofMillis(1));
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
            notifyExceptionEvent(new PlayerCancelActionExceptionEvent(e.getMessage()));

            sleepQuietly(Duration.ofMillis(1));
        } finally {
            var board = ((AbstractGame) this.game).getBoard();
            requestPlayerAction(board, event.getPlayer());
        }
    }

    private void notifyExceptionEvent(Event event) {
        // display error message to player
        ((Observable) this.game).notifyObservers(event);
    }

    private void requestPlayerAction(Board board, Player player) {
        // re-ask player about new action
        board.notifyObservers(new RequestPlayerActionEvent(player));
    }
}