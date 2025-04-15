package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.game.ai.SimulationGame;

abstract class AbstractSimulationGameEvaluator
        implements SimulationEvaluator {

    private final int limit;

    AbstractSimulationGameEvaluator(int limit) {
        this.limit = limit;
    }

    @Override
    public int evaluate(SimulationGame game) {
        var action = game.getAction();
        var sourcePiece = action.getPiece();

        var board = game.getBoard();
        var boardState = board.getState();

        var value = calculateValue(board, action, game.getColor());
        return boardState.isType(CHECK_MATED)
                ? 1000 * value * sourcePiece.getDirection()
                : boardState.getType().rank() * value;
    }

    protected int calculateValue(Board board, Action<?> action, Color color) {
        var sourcePiece = action.getPiece();
        var direction = sourcePiece.getDirection();

        var currentPlayerValue = board.calculateValue(color) * direction;
        var opponentPlayerValue = board.calculateValue(color.invert()) * Math.negateExact(direction);

        var value = action.getValue()                  // action type influence
                + this.limit * direction                    // depth influence
                + currentPlayerValue + opponentPlayerValue; // current board value

        return value;
    }
}