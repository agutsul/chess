package com.agutsul.chess.player.event;

import com.agutsul.chess.action.PiecePromoteAction;
import com.agutsul.chess.event.Event;

public class RequestPromotionPieceTypeEvent implements Event {

    private final PiecePromoteAction<?,?> action;

    public RequestPromotionPieceTypeEvent(PiecePromoteAction<?,?> action) {
        this.action = action;
    }

    public PiecePromoteAction<?,?> getAction() {
        return action;
    }
}