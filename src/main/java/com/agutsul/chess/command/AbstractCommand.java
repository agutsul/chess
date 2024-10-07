package com.agutsul.chess.command;

abstract class AbstractCommand implements Command {

    @Override
    public final void execute() {
//        try {
            preExecute();
            try {
                executeInternal();
            } finally {
                postExecute();
            }
//        } catch (Exception e) {
//            // TODO handle exception properly with proper logging
//            System.err.println(e);
//        }
    }

    // template methods

    // useful for validation before command execution
    protected void preExecute() {}
    // useful for event notification after execution
    protected void postExecute() {}

    // actual command execution
    protected abstract void executeInternal();
}