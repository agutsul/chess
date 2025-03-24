package com.agutsul.chess.board.event;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.piece.Piece;

public final class CopyVisitedPositionsEvent
        implements Event {

    private final Piece<Color> piece;

    public CopyVisitedPositionsEvent(Piece<Color> piece) {
        this.piece = piece;
    }

    public Piece<Color> getPiece() {
        return piece;
    }
}