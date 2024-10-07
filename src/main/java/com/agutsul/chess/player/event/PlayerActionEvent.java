package com.agutsul.chess.player.event;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Event;

public class PlayerActionEvent implements Event {

    private final Board board;
    private final String source;
    private final String target;

    public PlayerActionEvent(Board board, String source, String target) {
        this.board = board;
        this.source = source;
        this.target = target;
    }

    public Board getBoard() {
        return board;
    }
    public String getSource() {
        return source;
    }
    public String getTarget() {
        return target;
    }
}