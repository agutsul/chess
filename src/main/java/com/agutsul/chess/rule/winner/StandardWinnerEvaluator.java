package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DEFEAT;
import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DRAW;
import static com.agutsul.chess.board.state.BoardState.Type.AGREED_WIN;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.EXITED;
import static com.agutsul.chess.board.state.BoardState.Type.FIVE_FOLD_REPETITION;
import static com.agutsul.chess.board.state.BoardState.Type.SEVENTY_FIVE_MOVES;
import static com.agutsul.chess.board.state.BoardState.Type.STALE_MATED;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class StandardWinnerEvaluator
        extends AbstractWinnerEvaluator {

    private static final Logger LOGGER = getLogger(StandardWinnerEvaluator.class);

    public StandardWinnerEvaluator() {
        this(new WinnerScoreEvaluator());
    }

    StandardWinnerEvaluator(WinnerEvaluator winnerScoreEvaluator) {
        super(winnerScoreEvaluator);
    }

    @Override
    public Player evaluate(Game game) {
        var board = game.getBoard();
        var boardState = board.getState();

        LOGGER.info("Perform winner evaluation for player '{}' and board ({}:{})",
                game.getCurrentPlayer(), boardState.getColor(), boardState.getType());

        var winner = resolveWinner(game);
        if (nonNull(winner)) {
            LOGGER.info("Performed winner evaluation: winner - '{}'", winner);
        } else {
            LOGGER.info("No winner found for board state '{}': draw", board.getState());
        }

        return winner;
    }

    private Player resolveWinner(Game game) {
        var boardState = game.getBoard().getState();
        if (boardState.isAnyType(AGREED_DEFEAT, EXITED)) {
            return game.getOpponentPlayer();
        }

        if (boardState.isAnyType(CHECK_MATED, AGREED_WIN)) {
            return game.getCurrentPlayer();
        }

        if (boardState.isAnyType(AGREED_DRAW, FIVE_FOLD_REPETITION, SEVENTY_FIVE_MOVES, STALE_MATED)) {
            return null;
        }

        return super.evaluate(game);
    }
}