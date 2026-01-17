package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;

interface SimulationTask<ACTION extends Action<?>,
                         VALUE  extends Comparable<VALUE>,
                         RESULT extends TaskResult<ACTION,VALUE>> {

    RESULT simulate(ACTION action);
}