package com.agutsul.chess.piece;

import java.time.Instant;

public interface Captured {
    Instant getCapturedAt();
    void setCapturedAt(Instant instant);
}