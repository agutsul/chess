package com.agutsul.chess.player.event;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.event.Event;
import com.agutsul.chess.player.Player;

public class PlayerActionEvent
        implements Event {

    private final Player player;
    private final Board board;
    private final String source;
    private final String target;

    public PlayerActionEvent(Player player, Board board, String source, String target) {
        this.player = player;
        this.board = board;
        this.source = source;
        this.target = target;
    }

    public Player getPlayer() {
        return player;
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