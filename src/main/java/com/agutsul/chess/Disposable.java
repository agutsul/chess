package com.agutsul.chess;

import java.time.Instant;

public interface Disposable {
    // used to dispose promoted pawn
    void dispose();

    // used to dispose captured piece ( either by capture or by en-passante )
    void dispose(Instant instant);
}