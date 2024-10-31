package com.agutsul.chess.player.event;

import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;

public class PromotionPieceTypeEvent
        extends AbstractResponseEvent {

    private final Piece.Type pieceType;

    public PromotionPieceTypeEvent(Player player, Piece.Type pieceType) {
        super(player);
        this.pieceType = pieceType;
    }

    public Piece.Type getPieceType() {
        return pieceType;
    }
}