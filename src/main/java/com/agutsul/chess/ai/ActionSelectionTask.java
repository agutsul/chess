package com.agutsul.chess.ai;

import java.util.List;

import com.agutsul.chess.activity.action.Action;

interface ActionSelectionTask<ACTION extends Action<?>,
                              VALUE extends Comparable<VALUE>,
                              RESULT extends SimulationResult<ACTION,VALUE>> {

    RESULT process(List<List<ACTION>> actions);
}