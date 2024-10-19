package com.agutsul.chess.command;

import java.util.Objects;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.event.ActionCancelledEvent;
import com.agutsul.chess.action.event.ActionCancellingEvent;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.action.memento.CancelActionMementoFactory;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.AbstractGame;
import com.agutsul.chess.game.Game;

public class CancelActionCommand
        extends AbstractCommand {

    private final Game game;

    private Color color;
    private Action<?> action;

    public CancelActionCommand(Game game, Color color) {
        this.game = game;
        this.color = color;
    }

    @Override
    protected void preExecute() throws CommandException {
        var aGame = (AbstractGame) this.game;
        if (!aGame.hasPrevious()) {
            throw new CommandException("No action to cancel");
        }

        var journal = aGame.getJournal();
        // get last executed action from journal
        var actionMemento = (ActionMemento<?,?>) journal.get(journal.size() - 1);
        if (!Objects.equals(this.color, actionMemento.getColor())) {
            throw new CommandException("Unexpected player action");
        }

        this.action = createAction(aGame.getBoard(), actionMemento);
    }

    @Override
    protected void executeInternal() throws CommandException {
        ((Observable) this.game).notifyObservers(new ActionCancellingEvent(this.action));

        try {
            this.action.execute();
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }

        ((Observable) this.game).notifyObservers(new ActionCancelledEvent());
    }

    private Action<?> createAction(Board board, ActionMemento<?,?> memento) {
        return CancelActionMementoFactory.INSTANCE.create(board, memento);
    }
}