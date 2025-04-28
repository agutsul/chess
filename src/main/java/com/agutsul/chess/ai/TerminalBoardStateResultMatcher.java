package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;

final class TerminalBoardStateResultMatcher<ACTION extends Action<?>,
                                            VALUE extends Comparable<VALUE>,
                                            RESULT extends TaskResult<ACTION,VALUE>>
        implements ResultMatcher<ACTION,VALUE,RESULT> {

    @Override
    public boolean match(RESULT result) {
        var gameBoard = result.getBoard();
        var boardState = gameBoard.getState();

        return boardState.isTerminal();
    }
}