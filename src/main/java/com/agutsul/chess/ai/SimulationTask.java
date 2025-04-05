package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;

interface SimulationTask {
    Integer simulate(Action<?> action);
}