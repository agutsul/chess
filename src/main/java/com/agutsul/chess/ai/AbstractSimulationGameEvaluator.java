package com.agutsul.chess.ai;

import static com.agutsul.chess.board.state.BoardState.Type.CHECK_MATED;

import com.agutsul.chess.game.ai.SimulationGame;

abstract class AbstractSimulationGameEvaluator
        implements SimulationEvaluator {

    private final int limit;

    AbstractSimulationGameEvaluator(int limit) {
        this.limit = limit;
    }

    @Override
    public int evaluate(SimulationGame game) {
        var value = calculateValue(game);

        var action = game.getAction();
        var sourcePiece = action.getPiece();

        var board = game.getBoard();
        var boardState = board.getState();

        return boardState.isType(CHECK_MATED)
                ? 1000 * value * sourcePiece.getDirection()
                : boardState.getType().rank() * value;
    }

    protected int calculateValue(SimulationGame game) {
        var action = game.getAction();

        var sourcePiece = action.getPiece();
        var direction = sourcePiece.getDirection();

        var board = game.getBoard();
        var color = game.getColor();

        var currentPlayerValue = board.calculateValue(color) * direction;
        var opponentPlayerValue = board.calculateValue(color.invert()) * Math.negateExact(direction);

        var value = action.getValue()                  // action type influence
                + this.limit * direction                    // depth influence
                + currentPlayerValue + opponentPlayerValue; // current board value

        return value;
    }
}