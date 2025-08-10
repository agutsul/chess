package com.agutsul.chess.rule.winner;

import com.agutsul.chess.player.Player;

abstract class AbstractTimeoutWinnerEvaluator
        extends AbstractWinnerEvaluator {

    protected final Player player;

    AbstractTimeoutWinnerEvaluator(Player player) {
        this(new WinnerScoreEvaluator(), player);
    }

    AbstractTimeoutWinnerEvaluator(WinnerEvaluator winnerEvaluator, Player player) {
        super(winnerEvaluator);
        this.player = player;
    }
}