package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DEFEAT;
import static com.agutsul.chess.board.state.BoardState.Type.AGREED_DRAW;
import static com.agutsul.chess.board.state.BoardState.Type.AGREED_WIN;
import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.FIVE_FOLD_REPETITION;
import static com.agutsul.chess.board.state.BoardState.Type.SEVENTY_FIVE_MOVES;
import static com.agutsul.chess.board.state.BoardState.Type.STALE_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.TIMEOUT;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class WinnerEvaluatorImpl
        implements WinnerEvaluator<Player> {

    private static final Logger LOGGER = getLogger(WinnerEvaluatorImpl.class);

    @Override
    public Player evaluate(Game game) {
        var boardState = game.getBoard().getState();
        if (boardState.isAnyType(AGREED_DEFEAT, TIMEOUT)) {
            var player = game.getOpponentPlayer();
            LOGGER.info("{} wins. Player '{}'", player.getColor(), player.getName());
            return player;
        }

        if (boardState.isAnyType(CHECK_MATED, AGREED_WIN)) {
            var player = game.getCurrentPlayer();
            LOGGER.info("{} wins. Player '{}'", player.getColor(), player.getName());
            return game.getCurrentPlayer();
        }

        if (boardState.isAnyType(AGREED_DRAW, FIVE_FOLD_REPETITION, SEVENTY_FIVE_MOVES, STALE_MATED)) {
            LOGGER.info("Nobody wins, draw");
            return null;
        }

        // TODO: confirm winner detection algo
        return findWinner(game);
    }

    private Player findWinner(Game game) {
        var currentPlayerScore  = calculateScore(game.getBoard(), game.getCurrentPlayer());
        var opponentPlayerScore = calculateScore(game.getBoard(), game.getOpponentPlayer());

        var result = Integer.compare(currentPlayerScore, opponentPlayerScore);
        if (result == 0) {
            var currentPlayerActions  = calculateActions(game.getBoard(), game.getCurrentPlayer());
            var opponentPlayerActions = calculateActions(game.getBoard(), game.getOpponentPlayer());

            result = Integer.compare(currentPlayerActions, opponentPlayerActions);
            if (result == 0) {
                return null;
            }
        }

        return result > 0
                ? game.getCurrentPlayer()
                : game.getOpponentPlayer();
    }

    private int calculateActions(Board board, Player player) {
        var pieces = board.getPieces(player.getColor());

        int result = 0;
        for (var action : Action.Type.values()) {
            result += pieces.stream()
                    .map(piece -> board.getActions(piece, action))
                    .mapToInt(Collection::size)
                    .sum();
        }

        return result;
    }

    private int calculateScore(Board board, Player player) {
        return Math.abs(board.calculateValue(player.getColor()));
    }
}