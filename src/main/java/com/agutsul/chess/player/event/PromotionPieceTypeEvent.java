package com.agutsul.chess.player.event;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.piece.Piece;

public class PromotionPieceTypeEvent implements Event {

    private final Piece.Type pieceType;

    public PromotionPieceTypeEvent(Piece.Type pieceType) {
        this.pieceType = pieceType;
    }

    public Piece.Type getPieceType() {
        return pieceType;
    }
}