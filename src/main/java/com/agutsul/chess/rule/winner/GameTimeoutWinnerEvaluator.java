package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardState.Type.TIMEOUT;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class GameTimeoutWinnerEvaluator
        extends AbstractWinnerEvaluator {

    private static final Logger LOGGER = getLogger(GameTimeoutWinnerEvaluator.class);

    public GameTimeoutWinnerEvaluator() {
        this(new WinnerScoreEvaluator());
    }

    GameTimeoutWinnerEvaluator(WinnerEvaluator winnerEvaluator) {
        super(winnerEvaluator);
    }

    @Override
    public Player evaluate(Game game) {
        var board = game.getBoard();
        var boardState = board.getState();

        if (boardState.isType(TIMEOUT)) {
            var opponentPlayer = game.getOpponentPlayer();
            LOGGER.info("{} wins. Player '{}'", opponentPlayer.getColor(), opponentPlayer.getName());
            return opponentPlayer;
        }

        LOGGER.info("Perform player score comparison to resolve winner");
        return super.evaluate(game);
    }
}