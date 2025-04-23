package com.agutsul.chess.ai;

import com.agutsul.chess.game.ai.SimulationGame;

public interface SimulationEvaluator<T extends Comparable<T>> {
    T evaluate(SimulationGame game);
}