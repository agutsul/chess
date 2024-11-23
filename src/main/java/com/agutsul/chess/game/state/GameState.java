package com.agutsul.chess.game.state;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.state.State;

public interface GameState
        extends State<Game> {

    enum Type {
        UNKNOWN("*"),
        WHITE_WIN("1-0"),
        BLACK_WIN("0-1"),
        DRAWN_GAME("1/2-1/2");

        private static final Map<String,Type> CODES = Stream.of(values())
                .collect(toMap(Type::code, identity()));

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

        public static Type codeOf(String code) {
            return CODES.getOrDefault(lowerCase(code), UNKNOWN);
        }
    }

    Type getType();
}