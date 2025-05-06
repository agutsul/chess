package com.agutsul.chess.color;

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
}