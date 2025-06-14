package com.agutsul.chess.player.event;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.event.Observer;

public class RequestPromotionPieceTypeEvent
        extends AbstractRequestEvent {

    private final Observer observer;

    public RequestPromotionPieceTypeEvent(Color color, Observer observer) {
        super(color);
        this.observer = observer;
    }

    public Observer getObserver() {
        return observer;
    }
}