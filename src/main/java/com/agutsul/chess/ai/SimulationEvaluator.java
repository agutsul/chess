package com.agutsul.chess.ai;

import com.agutsul.chess.game.ai.SimulationGame;

public interface SimulationEvaluator {
    int evaluate(SimulationGame game);
}