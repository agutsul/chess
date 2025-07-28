package com.agutsul.chess.game.state;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.state.State;

public interface GameState
        extends State<Game> {

    enum Type {
        UNKNOWN("*", null),
        WHITE_WIN("1-0", Colors.WHITE),
        BLACK_WIN("0-1", Colors.BLACK),
        DRAWN_GAME("1/2-1/2", null);

        private static final Map<String,Type> CODES = Stream.of(values())
                .collect(toMap(Type::code, identity()));

        private String code;
        private Color color;    // winner color

        Type(String code, Color color) {
            this.code = code;
            this.color = color;
        }

        public Optional<Color> color() {
            return Optional.ofNullable(color);
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

    // utilities

    static boolean isUnknown(GameState gameState) {
        return isUnknown(gameState.getType());
    }

    static boolean isUnknown(GameState.Type type) {
        return GameState.Type.UNKNOWN.equals(type);
    }

    static boolean isWhiteWin(GameState gameState) {
        return isWhiteWin(gameState.getType());
    }

    static boolean isWhiteWin(GameState.Type type) {
        return GameState.Type.WHITE_WIN.equals(type);
    }

    static boolean isBlackWin(GameState gameState) {
        return isWhiteWin(gameState.getType());
    }

    static boolean isBlackWin(GameState.Type type) {
        return GameState.Type.BLACK_WIN.equals(type);
    }

    static boolean isDrawn(GameState gameState) {
        return isUnknown(gameState.getType());
    }

    static boolean isDrawn(GameState.Type type) {
        return GameState.Type.DRAWN_GAME.equals(type);
    }
}