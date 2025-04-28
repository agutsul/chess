package com.agutsul.chess.ai;

import com.agutsul.chess.game.ai.SimulationGame;

public interface SimulationEvaluator<VALUE extends Comparable<VALUE>> {
    VALUE evaluate(SimulationGame game);
}