package com.agutsul.chess.rule.winner;

import static com.agutsul.chess.board.state.BoardState.Type.INSUFFICIENT_MATERIAL;
import static com.agutsul.chess.board.state.BoardState.Type.TIMEOUT;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;

import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.board.state.CompositeBoardState;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState;
import com.agutsul.chess.board.state.InsufficientMaterialBoardState.Pattern;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;

public final class ActionTimeoutWinnerEvaluator
        implements WinnerEvaluator {

    private static final Logger LOGGER = getLogger(ActionTimeoutWinnerEvaluator.class);

    private static final List<Pattern> INSUFFICIENT_MATERIAL_PATTERNS = List.of(
            Pattern.SINGLE_KING,
            Pattern.KING_AND_KNIGHT_VS_KING_AND_QUEEN,
            Pattern.BISHOP_POSITION_COLOR_VS_KING_POSITION_COLOR
    );

    private final WinnerEvaluator playerScoreEvaluator;

    public ActionTimeoutWinnerEvaluator() {
        this(new PlayerScoreWinnerEvaluator());
    }

    ActionTimeoutWinnerEvaluator(WinnerEvaluator playerScoreEvaluator) {
        this.playerScoreEvaluator = playerScoreEvaluator;
    }

    @Override
    public Player evaluate(Game game) {
        var board = game.getBoard();
        var boardState = board.getState();

        if (boardState.isType(TIMEOUT)) {
            var opponentPlayer = game.getOpponentPlayer();

            var opponentBoardState = board.getState(opponentPlayer.getColor());
            if (opponentBoardState == null || isInsufficientMaterial(opponentBoardState)) {
                // so opponent is unable to win and the best result is a draw
                LOGGER.info("No winner found for board state '{}': draw", board.getState());
                return null;
            }

            LOGGER.info("{} wins. Player '{}'", opponentPlayer.getColor(), opponentPlayer.getName());
            return opponentPlayer;
        }

        LOGGER.info("Perform player score comparison to resolve winner");
        return this.playerScoreEvaluator.evaluate(game);
    }


    private static boolean isInsufficientMaterial(BoardState boardState) {
        return INSUFFICIENT_MATERIAL_PATTERNS.stream()
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