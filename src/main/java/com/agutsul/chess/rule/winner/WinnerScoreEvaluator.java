package com.agutsul.chess.rule.winner;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

final class WinnerScoreEvaluator
        implements WinnerEvaluator {

    @Override
    // TODO: confirm winner detection algo
    public Player evaluate(Game game) {
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
        int result = Stream.of(Action.Type.values())
                .map(action -> Stream.of(pieces)
                        .flatMap(Collection::stream)
                        .map(piece -> board.getActions(piece, action))
                        .mapToInt(Collection::size)
                        .sum()
                )
                .mapToInt(Integer::intValue)
                .sum();

        return result;
    }

    private int calculateScore(Board board, Player player) {
        return Math.abs(board.calculateValue(player.getColor()));
    }
}