package com.agutsul.chess.player;

import static org.apache.commons.lang3.StringUtils.lowerCase;

public enum PlayerCommand {
    UNDO,
    DRAW,
    WIN,
    DEFEAT,
    EXIT;

    private String code;

    PlayerCommand() {
        this.code = lowerCase(name());
    }

    public String code() {
        return this.code;
    }

    @Override
    public String toString() {
        return code();
    }
}