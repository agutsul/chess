package com.agutsul.chess.piece.state;

import java.time.Instant;

import com.agutsul.chess.Disposable;
import com.agutsul.chess.piece.Piece;

public interface DisposedPieceState<PIECE extends Piece<?> & Disposable> {
    Instant getDisposedAt();
}