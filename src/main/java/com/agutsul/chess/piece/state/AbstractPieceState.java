package com.agutsul.chess.piece.state;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Movable;
import com.agutsul.chess.piece.Piece;

public abstract class AbstractPieceState<PIECE extends Piece<Color> & Movable & Capturable>
        implements PieceState<PIECE>,
                   MovablePieceState<PIECE>,
                   CapturablePieceState<PIECE> {

    protected final Type type;

    public AbstractPieceState(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.name();
    }
}