package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;

final class TerminalBoardStateResultMatcher<RESULT extends TaskResult<Action<?>,Integer>>
        implements ResultMatcher<Action<?>,Integer,RESULT> {

    @Override
    public boolean match(RESULT result) {
        var gameBoard = result.getBoard();
        var boardState = gameBoard.getState();

        return boardState.isTerminal();
    }
}