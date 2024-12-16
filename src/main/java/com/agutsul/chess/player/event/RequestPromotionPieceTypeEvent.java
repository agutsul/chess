package com.agutsul.chess.player.event;

import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.color.Color;

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