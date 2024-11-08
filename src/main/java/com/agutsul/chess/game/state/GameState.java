package com.agutsul.chess.game.state;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.state.State;

public interface GameState
        extends State<Game> {

    enum Type {
        ASTERISK("*"),
        WHITE_WIN("1-0"),
        BLACK_WIN("0-1"),
        DRAWN_GAME("1/2-1/2");

        private String code;

        Type(String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        @Override
        public String toString() {
            return code();
        }
    }

    Type getType();
}