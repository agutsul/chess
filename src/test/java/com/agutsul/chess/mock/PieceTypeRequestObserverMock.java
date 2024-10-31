package com.agutsul.chess.mock;

import static org.mockito.Mockito.mock;

import com.agutsul.chess.event.Event;
import com.agutsul.chess.event.Observer;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.event.PromotionPieceTypeEvent;
import com.agutsul.chess.player.event.RequestPromotionPieceTypeEvent;

public class PieceTypeRequestObserverMock
        implements Observer {

    private static final Event PROMOTION_EVENT =
            new PromotionPieceTypeEvent(mock(Player.class), Piece.Type.QUEEN);

    @Override
    public void observe(Event event) {
        if (event instanceof RequestPromotionPieceTypeEvent) {
            process((RequestPromotionPieceTypeEvent) event);
        }
    }

    private void process(RequestPromotionPieceTypeEvent event) {
        // mock interaction with user during piece type selection
        event.getAction().observe(PROMOTION_EVENT);
    }
}