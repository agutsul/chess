package com.agutsul.chess.command;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.exception.CommandException;

abstract class AbstractCommand
        implements Command {

    private static final Logger LOGGER = getLogger(AbstractCommand.class);

    @Override
    public final void execute() {
        try {
            preExecute();
            try {
                executeInternal();
            } finally {
                postExecute();
            }
        } catch (CommandException e) {
            LOGGER.error("Command exception", e);
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    // template methods

    // useful for validation before command execution
    protected void preExecute() {}
    // useful for event notification after execution
    protected void postExecute() {}

    // actual command execution
    protected abstract void executeInternal() throws CommandException;
}