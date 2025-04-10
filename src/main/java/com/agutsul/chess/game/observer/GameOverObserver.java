package com.agutsul.chess.game.observer;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observable;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.game.event.GameOverEvent;

public final class GameOverObserver
        implements Observer {

    @Override
    public void observe(Event event) {
        if (event instanceof GameOverEvent) {
            process((GameOverEvent) event);
        }
    }

    private void process(GameOverEvent event) {
        var game = event.getGame();

        // force closing fork-join pool
        try (var pool = game.getForkJoinPool()) {
            notifyBoardObservers(game.getBoard(), event);
        }
    }

    private static void notifyBoardObservers(Board board, Event event) {
        ((Observable) board).notifyObservers(event);
    }
}