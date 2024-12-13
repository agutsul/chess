package com.agutsul.chess;

import java.time.Instant;

public interface Captured {
    Instant getCapturedAt();
    void setCapturedAt(Instant instant);
}