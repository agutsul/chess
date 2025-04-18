package com.agutsul.chess.ai;

import java.util.List;

import com.agutsul.chess.activity.action.Action;

interface ActionSelectionTask<ACTION extends Action<?>,
                              RESULT  extends SimulationResult<ACTION>> {

    RESULT process(List<List<ACTION>> actions);
}