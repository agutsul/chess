package com.agutsul.chess.color;

import java.util.Objects;

public enum Colors implements Color {
    WHITE {
        @Override
        public Color invert() {
            return BLACK;
        }
    },
    BLACK {
        @Override
        public Color invert() {
            return WHITE;
        }
    };

    @Override
    public String toString() {
        return name();
    }

    // utilities

    public static boolean isWhite(Color color) {
        return Colors.WHITE.equals(color);
    }

    public static boolean isBlack(Color color) {
        return Colors.BLACK.equals(color);
    }

    public static boolean isEqual(Color color1, Color color2) {
        return Objects.equals(color1, color2);
    }
}