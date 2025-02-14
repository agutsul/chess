package com.agutsul.chess.piece.state;

import java.time.Instant;

import com.agutsul.chess.Disposable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.state.State;

public interface DisposedPieceState<PIECE extends Piece<?> & Disposable>
        extends State<PIECE> {

    Instant getDisposedAt();
}