package com.agutsul.chess.antlr.pgn.action;

import com.agutsul.chess.activity.action.adapter.ActionAdapter;

public interface PgnActionAdapter
        extends ActionAdapter {

    String adapt(String action);
}