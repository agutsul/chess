package com.agutsul.chess.ai;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;

final class PlayerBoardStateResultMatcher<RESULT extends TaskResult<Action<?>,Integer>>
        implements ResultMatcher<Action<?>,Integer,RESULT> {

    private final Color color;
    private final BoardState.Type boardState;

    private final AtomicBoolean found;

    PlayerBoardStateResultMatcher(Color color, BoardState.Type boardState) {
        this.color = color;
        this.boardState = boardState;
        this.found = new AtomicBoolean(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean match(RESULT result) {
        if (found.get()) {
            return true;
        }

        var opponentResult = result.getOpponentResult();
        if (Objects.nonNull(opponentResult)) {
            return match((RESULT) opponentResult);
        }

        var board = result.getBoard();
        var boardState = board.getState();

        var journal = result.getJournal();
        var actionMemento = journal.getLast();

        var matched = Objects.equals(this.color, actionMemento.getColor())
                && boardState.isType(this.boardState);

        if (matched) {
            found.set(matched);
        }

        return matched;
    }
}