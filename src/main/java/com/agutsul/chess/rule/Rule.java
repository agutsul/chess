package com.agutsul.chess.rule;

public interface Rule<SOURCE,RESULT> {
    RESULT evaluate(SOURCE source);
}
