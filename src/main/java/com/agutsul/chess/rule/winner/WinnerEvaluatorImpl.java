package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DEFEAT;
import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DRAW;
import static com.agutsul.chess.board.state.BoardState.Type.AGREED_WIN;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.FIVE_FOLD_REPETITION;
import static com.agutsul.chess.board.state.BoardState.Type.SEVENTY_FIVE_MOVES;
import static com.agutsul.chess.board.state.BoardState.Type.STALE_MATED;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class WinnerEvaluatorImpl
        extends AbstractWinnerEvaluator {

    private static final Logger LOGGER = getLogger(WinnerEvaluatorImpl.class);

    public WinnerEvaluatorImpl() {
        this(new WinnerScoreEvaluator());
    }

    WinnerEvaluatorImpl(WinnerEvaluator winnerScoreEvaluator) {
        super(winnerScoreEvaluator);
    }

    @Override
    // returns winner player
    public Player evaluate(Game game) {
        var board = game.getBoard();
        var boardState = board.getState();

        if (boardState.isAnyType(AGREED_DEFEAT)) {
            var opponentPlayer = game.getOpponentPlayer();
            LOGGER.info("{} wins. Player '{}'", opponentPlayer.getColor(), opponentPlayer.getName());
            return opponentPlayer;
        }

        if (boardState.isAnyType(CHECK_MATED, AGREED_WIN)) {
            var currentPlayer = game.getCurrentPlayer();
            LOGGER.info("{} wins. Player '{}'", currentPlayer.getColor(), currentPlayer.getName());
            return currentPlayer;
        }

        if (boardState.isAnyType(AGREED_DRAW, FIVE_FOLD_REPETITION, SEVENTY_FIVE_MOVES, STALE_MATED)) {
            LOGGER.info("No winner found for board state '{}': draw", board.getState());
            return null;
        }

        LOGGER.info("Perform player score comparison to resolve winner");
        return super.evaluate(game);
    }
}