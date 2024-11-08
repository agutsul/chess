package com.agutsul.chess.rule.board;

import java.util.HashMap;
import java.util.Map;

import com.agutsul.chess.action.event.AbstractProccessedActionEvent;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;

final class CachedBoardStateEvaluator
        implements BoardStateEvaluator<BoardState> {

    private final Map<Color, BoardState> cache = new HashMap<>();

    private final BoardStateEvaluator<BoardState> evaluator;

    public CachedBoardStateEvaluator(Board board, BoardStateEvaluator<BoardState> evaluator) {
        this.evaluator = evaluator;
        ((Observable) board).addObserver(new BoardStateObserver());
    }

    @Override
    public BoardState evaluate(Color color) {
        if (cache.containsKey(color)) {
            return cache.get(color);
        }

        var boardState = evaluator.evaluate(color);
        cache.put(color, boardState);

        return boardState;
    }

    private final class BoardStateObserver
            implements Observer {

        @Override
        public void observe(Event event) {
            if (event instanceof AbstractProccessedActionEvent) {
                // clear cached calculated board states
                // to force its recalculation for the new board state
                cache.clear();
            }
        }
    }
}
