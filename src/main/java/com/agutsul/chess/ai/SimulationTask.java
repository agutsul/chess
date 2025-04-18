package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;

interface SimulationTask<ACTION extends Action<?>,RESULT extends SimulationResult<ACTION>> {
    RESULT simulate(ACTION action);
}