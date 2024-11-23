package com.agutsul.chess.position;

public interface Position
        extends Calculated {

    String[] LABELS = { "a", "b", "c", "d", "e", "f", "g", "h" };

    int MAX = 8;
    int MIN = 0;

    int x();
    int y();

    static String codeOf(int x, int y) {
        if (x < MIN || y < MIN || x >= MAX || y >= MAX) {
            return null;
        }

        return String.format("%s%d", LABELS[x], y + 1);
    }

    static String codeOf(Position position) {
        return codeOf(position.x(), position.y());
    }
}