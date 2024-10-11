package com.agutsul.chess;

public enum Colors implements Color {
    WHITE("#FFFFFF") {
        @Override
        public Color invert() {
            return BLACK;
        }
    },
    BLACK("#000000") {
        @Override
        public Color invert() {
            return WHITE;
        }
    };

    // RGB code
    private String code;

    Colors(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    @Override
    public String toString() {
        return name();
    }
}