package com.agutsul.chess.command;

import org.slf4j.Logger;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

abstract class AbstractUpdateBoardStateCommand
        extends AbstractCommand {

    protected final Game game;
    protected final Player player;

    AbstractUpdateBoardStateCommand(Logger logger, Game game, Player player) {
        super(logger);
        this.game = game;
        this.player = player;
    }

    @Override
    protected final void executeInternal() throws CommandException {
        notifyGameObservers(createPreExecutionEvent());

        try {
            updateBoardState();
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }

        notifyGameObservers(createPostExecutionEvent());
    }

    protected abstract Event createPreExecutionEvent();

    protected abstract Event createPostExecutionEvent();

    protected abstract void updateBoardState();

    private void notifyGameObservers(Event event) {
        ((Observable) this.game).notifyObservers(event);
    }
}