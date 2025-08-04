package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;
import static com.agutsul.chess.board.state.BoardState.Type.INSUFFICIENT_MATERIAL;
import static com.agutsul.chess.board.state.BoardState.Type.TIMEOUT;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.ai.ActionSelectionStrategy;
import com.agutsul.chess.ai.SelectionStrategy;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState.Pattern;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class ActionTimeoutWinnerEvaluator
        extends AbstractWinnerEvaluator {

    private static final Logger LOGGER = getLogger(ActionTimeoutWinnerEvaluator.class);

    public ActionTimeoutWinnerEvaluator() {
        this(new WinnerScoreEvaluator());
    }

    ActionTimeoutWinnerEvaluator(WinnerEvaluator winnerScoreEvaluator) {
        super(winnerScoreEvaluator);
    }

    @Override
    public Player evaluate(Game game) {
        var board = game.getBoard();
        var boardState = board.getState();

        if (boardState.isType(TIMEOUT)) {
            var opponentPlayer = game.getOpponentPlayer();

            var opponentBoardState = board.getState(opponentPlayer.getColor());
            if (opponentBoardState == null) {
                LOGGER.info("No opponent action performed: draw");
                return null;
            }

            // opponent quick check if it there is material for a checkmate
            if (isInsufficientMaterial(opponentBoardState)) {
                LOGGER.info("No winner found for board state '{}': draw", board.getState());
                return null;
            }

            try (var forkJoinPool = new ForkJoinPool()) {
                var selectionStrategy = new ActionSelectionStrategy(
                        board, game.getJournal(), forkJoinPool, SelectionStrategy.Type.ALPHA_BETA
                );

                // check if any opponent's checkmate action flow exists
                var opponentCheckMateAction = selectionStrategy.select(
                        opponentPlayer.getColor(), CHECK_MATED
                );

                if (opponentCheckMateAction.isEmpty()) {
                    // so opponent is unable to win and the best result is a draw
                    LOGGER.info("{} Player '{}' unable to checkmate",
                            opponentPlayer.getColor(), opponentPlayer.getName()
                    );

                    return null;
                }
            }

            LOGGER.info("{} wins. Player '{}'",
                    opponentPlayer.getColor(), opponentPlayer.getName()
            );

            return opponentPlayer;
        }

        LOGGER.info("Perform player score comparison to resolve winner");
        return super.evaluate(game);
    }

    private static boolean isInsufficientMaterial(BoardState boardState) {
        return Stream.of(Pattern.values())
                .anyMatch(pattern -> isInsufficientMaterial(boardState, pattern));
    }

    private static boolean isInsufficientMaterial(BoardState boardState, Pattern pattern) {
        if (boardState instanceof CompositeBoardState) {
            var boardStates = ((CompositeBoardState) boardState).getBoardStates();
            return boardStates.stream()
                    .anyMatch(state -> isInsufficientMaterial(state, pattern));
        }

        var isInsufficientMaterial = boardState.isType(INSUFFICIENT_MATERIAL)
                && Objects.equals(((InsufficientMaterialBoardState) boardState).getPattern(), pattern);

        return isInsufficientMaterial;
    }
}