package com.agutsul.chess;

public enum Colors implements Color {
    WHITE("#FFFFFF") {
        @Override
        public Color invert() {
            return Colors.BLACK;
        }
    },
    BLACK("#000000") {
        @Override
        public Color invert() {
            return Colors.WHITE;
        }
    };

    // RGB code
    private String code;

    private Colors(String code) {
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