package com.agutsul.chess.player.event;

import com.agutsul.chess.Color;
import com.agutsul.chess.action.PiecePromoteAction;

public class RequestPromotionPieceTypeEvent
        extends AbstractRequestEvent {

    private final PiecePromoteAction<?,?> action;

    public RequestPromotionPieceTypeEvent(Color color, PiecePromoteAction<?,?> action) {
        super(color);
        this.action = action;
    }

    public PiecePromoteAction<?,?> getAction() {
        return action;
    }
}