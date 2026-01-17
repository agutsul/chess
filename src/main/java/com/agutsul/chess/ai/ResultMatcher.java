package com.agutsul.chess.ai;

import com.agutsul.chess.activity.action.Action;

interface ResultMatcher<ACTION extends Action<?>,
                        VALUE  extends Comparable<VALUE>,
                        RESULT extends TaskResult<ACTION,VALUE>> {

    boolean match(RESULT result);
}