package com.agutsul.chess.position;

public interface Position extends Calculated {

    String[] LABELS = { "a", "b", "c", "d", "e", "f", "g", "h" };

    int MAX = 8;
    int MIN = 0;

    int x();
    int y();
}
