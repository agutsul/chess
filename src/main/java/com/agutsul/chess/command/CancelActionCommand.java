package com.agutsul.chess.command;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Objects;

import org.slf4j.Logger;

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

    private static final Logger LOGGER = getLogger(CancelActionCommand.class);

    private static final String UNEXPECTED_ACTION_MESSAGE = "Unexpected player action";
    private static final String NOTHING_TO_CANCEL_MESSAGE = "No action to cancel";

    private final Game game;
    private final Color color;

    private Action<?> action;

    public CancelActionCommand(Game game, Color color) {
        super(LOGGER);
        this.game = game;
        this.color = color;
    }

    @Override
    protected void preExecute() throws CommandException {
        var aGame = (AbstractGame) this.game;
        if (!aGame.hasPrevious()) {
            throw new CommandException(NOTHING_TO_CANCEL_MESSAGE);
        }

        var journal = aGame.getJournal();
        // get last executed action from journal
        var actionMemento = (ActionMemento<?,?>) journal.get(journal.size() - 1);
        if (!Objects.equals(this.color, actionMemento.getColor())) {
            throw new CommandException(UNEXPECTED_ACTION_MESSAGE);
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

    private static Action<?> createAction(Board board, ActionMemento<?,?> memento) {
        return CancelActionMementoFactory.INSTANCE.create(board, memento);
    }
}