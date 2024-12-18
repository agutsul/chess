package com.agutsul.chess.game.event;

import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.state.BoardState;
import com.agutsul.chess.event.Event;

public class BoardStateNotificationEvent
        implements Event {

    private final BoardState boardState;
    private final ActionMemento<?,?> memento;

    public BoardStateNotificationEvent(BoardState boardState,
                                       ActionMemento<?,?> memento) {
        this.boardState = boardState;
        this.memento = memento;
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public ActionMemento<?,?> getMemento() {
        return memento;
    }
}