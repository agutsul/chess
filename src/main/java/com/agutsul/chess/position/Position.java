package com.agutsul.chess.position;

import java.io.Serializable;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Placeable;
import com.agutsul.chess.color.Color;

public interface Position
        extends Calculatable, Placeable, Serializable {

    String[] LABELS = { "a", "b", "c", "d", "e", "f", "g", "h" };

    int MAX = 8;
    int MIN = 0;

    int x();
    int y();

    Color getColor();

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