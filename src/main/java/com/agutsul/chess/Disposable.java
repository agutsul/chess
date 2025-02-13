package com.agutsul.chess;

import java.time.Instant;

public interface Disposable {
    // used to dispose promoted pawn
    void dispose();

    // used to dispose captured piece
    void dispose(Instant instant);
}