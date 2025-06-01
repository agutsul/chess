package com.agutsul.chess.rule.winner;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

abstract class AbstractWinnerEvaluator
        implements WinnerEvaluator {

    private final WinnerEvaluator winnerEvaluator;

    AbstractWinnerEvaluator(WinnerEvaluator winnerEvaluator) {
        this.winnerEvaluator = winnerEvaluator;
    }

    @Override
    public Player evaluate(Game game) {
        return this.winnerEvaluator.evaluate(game);
    }
}