package com.agutsul.chess.rule.board;

import java.util.HashMap;
import java.util.Map;

import com.agutsul.chess.action.event.AbstractProccessedActionEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;

public class CachedBoardStateEvaluator
        implements BoardStateEvaluator {

    private final Map<Color, BoardState> boardStateCache = new HashMap<>();

    private final BoardStateEvaluator evaluator;

    public CachedBoardStateEvaluator(Board board) {
        this.evaluator = new BoardStateEvaluatorImpl(board);
        board.addObserver(new BoardStateObserver());
    }

    @Override
    public BoardState evaluate(Color playerColor) {
        if (boardStateCache.containsKey(playerColor)) {
            return boardStateCache.get(playerColor);
        }

        var boardState = evaluator.evaluate(playerColor);
        boardStateCache.put(playerColor, boardState);

        return boardState;
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
