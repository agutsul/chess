package com.agutsul.chess.game.event;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.rule.winner.WinnerEvaluator.Type;

public class GameWinnerEvent
        extends AbstractGameEvent {

    private final Type type;
    private final Player player;

    public GameWinnerEvent(Game game, Type type) {
        this(game, game.getCurrentPlayer(), type);
    }

    public GameWinnerEvent(Game game, Player player, Type type) {
        super(game);

        this.player = player;
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public Player getPlayer() {
        return this.player;
    }
}