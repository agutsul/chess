package com.agutsul.chess.rule.board;

import java.util.HashMap;
import java.util.Map;

import com.agutsul.chess.action.event.AbstractProccessedActionEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CheckMatedBoardState;
import com.agutsul.chess.board.state.CheckedBoardState;
import com.agutsul.chess.board.state.DefaultBoardState;
import com.agutsul.chess.board.state.StaleMatedBoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;

public final class BoardStateEvaluatorImpl
        implements BoardStateEvaluator {

    private final Map<Color, BoardState> boardStateCache = new HashMap<>();

    private final Board board;

    public BoardStateEvaluatorImpl(Board board) {
        this.board = board;
        this.board.addObserver(new BoardStateObserver());
    }

    @Override
    public BoardState evaluate(Color playerColor) {
        if (boardStateCache.containsKey(playerColor)) {
            return boardStateCache.get(playerColor);
        }

        var boardState = calculate(playerColor);
        boardStateCache.put(playerColor, boardState);

        return boardState;
    }

    private BoardState calculate(Color playerColor) {
        if (board.isChecked(playerColor)) {

            if (board.isCheckMated(playerColor)) {
                return new CheckMatedBoardState(board, playerColor);
            }

            return new CheckedBoardState(board, playerColor);
        }

        if (board.isStaleMated(playerColor)) {
            return new StaleMatedBoardState(board, playerColor);
        }

        return new DefaultBoardState(board, playerColor);
    }

    private final class BoardStateObserver
            implements Observer {

        @Override
        public void observe(Event event) {
            if (event instanceof AbstractProccessedActionEvent) {
                // clear cached calculated board states
                // to force its recalculation for the new board state
                boardStateCache.clear();
            }
        }
    }
}