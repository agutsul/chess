package com.agutsul.chess.command;

import org.slf4j.Logger;

import com.agutsul.chess.exception.CommandException;
import com.agutsul.chess.exception.IllegalActionException;

abstract class AbstractCommand
        implements Command {

    private final Logger logger;

    protected AbstractCommand(Logger logger) {
        this.logger = logger;
    }

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
            logger.error("Command exception", e);
            throw new IllegalActionException(e.getMessage());
        }
    }

    // template methods

    // useful for validation before command execution
    protected void preExecute() throws CommandException {}
    // useful for event notification after execution
    protected void postExecute() throws CommandException {}

    // actual command execution
    protected abstract void executeInternal() throws CommandException;
}