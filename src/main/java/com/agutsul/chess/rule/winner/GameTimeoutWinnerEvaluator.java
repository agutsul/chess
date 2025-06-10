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

        LOGGER.info("Perform winner evaluation for player {} and board ({}:{})",
                game.getCurrentPlayer(), boardState.getColor(), boardState.getType());

        var winner = boardState.isType(TIMEOUT)
                ? game.getOpponentPlayer()
                : super.evaluate(game);

        if (winner != null) {
            LOGGER.info("Performed winner evaluation: winner - '{}'", winner);
        } else {
            LOGGER.info("No winner found for board state '{}': draw", board.getState());
        }

        return winner;
    }
}