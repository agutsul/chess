package com.agutsul.chess.mock;

import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

public class PlayerInputObserverMock
        extends AbstractPlayerInputObserver {

    private final String actionCommand;
    private final String promotionType;

    public PlayerInputObserverMock(Player player, Game game) {
        this(player, game, null);
    }

    public PlayerInputObserverMock(Player player, Game game, String actionCommand) {
        this(player, game, actionCommand, null);
    }

    public PlayerInputObserverMock(Player player, Game game,
                                   String actionCommand, String promotionType) {

        super(player, game);

        this.actionCommand = actionCommand;
        this.promotionType = promotionType;
    }

    @Override
    protected String getActionCommand() {
        return actionCommand;
    }

    @Override
    protected String getPromotionPieceType() {
        return promotionType;
    }
}