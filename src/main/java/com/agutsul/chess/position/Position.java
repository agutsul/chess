package com.agutsul.chess.position;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public interface Position
        extends Calculated {

    @SuppressFBWarnings("MS_MUTABLE_ARRAY")
    String[] LABELS = { "a", "b", "c", "d", "e", "f", "g", "h" };

    int MAX = 8;
    int MIN = 0;

    int x();
    int y();
}