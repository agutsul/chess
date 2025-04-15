package com.agutsul.chess.ai;

import java.util.List;

import com.agutsul.chess.activity.action.Action;

interface ActionSelectionTask<RESULT,ACTION extends Action<?>> {
    RESULT process(List<List<ACTION>> actions);
}