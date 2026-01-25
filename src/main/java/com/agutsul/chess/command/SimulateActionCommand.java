package com.agutsul.chess.command;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.split;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.ActionAdapter;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.game.Game;

public final class SimulateActionCommand
        extends AbstractCommand
        implements ActionAdapter {

    private static final Logger LOGGER = getLogger(SimulateActionCommand.class);

    private static final String UNSUPPORTED_COMMAND_ERROR_MESSAGE = "Unsupported command format";

    private final PerformActionCommand command;
    private final Action<?> action;

    public SimulateActionCommand(Game game, Action<?> action) {
        this(createCommand(game), action);
    }

    SimulateActionCommand(PerformActionCommand command, Action<?> action) {
        super(LOGGER);

        this.command = command;
        this.action  = action;
    }

    @Override
    public String toString() {
        return String.format("%s", this.action);
    }

    @Override
    protected void preExecute() throws CommandException {
        var command = adapt(this.action);

        var positions = split(command, SPACE);
        if (positions.length != 2) {
            throw new IllegalStateException(String.format(
                    "%s: '%s'",
                    UNSUPPORTED_COMMAND_ERROR_MESSAGE,
                    command
            ));
        }

        this.command.setSource(positions[0]);
        this.command.setTarget(positions[1]);
    }

    @Override
    protected void executeInternal() throws CommandException {
        this.command.execute();
    }

    private static PerformActionCommand createCommand(Game game) {
        return new PerformActionCommand(
                game.getCurrentPlayer(),
                game.getBoard(),
                (Observable) game
        );
    }
}