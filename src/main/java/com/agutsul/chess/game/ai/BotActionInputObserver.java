package com.agutsul.chess.game.ai;

import static com.agutsul.chess.activity.action.Action.isPromote;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.agutsul.chess.activity.action.ActionAdapter;
import com.agutsul.chess.activity.action.PiecePromoteAction;
import com.agutsul.chess.ai.ActionSelectionStrategy;
import com.agutsul.chess.ai.AlphaBetaActionSelectionStrategy;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

public final class BotActionInputObserver
        extends AbstractPlayerInputObserver
        implements ActionAdapter {

    private static final Logger LOGGER = getLogger(BotActionInputObserver.class);

    private final ActionSelectionStrategy actionStrategy;

    private PiecePromoteAction<?,?> promoteAction;

    public BotActionInputObserver(Player player, Game game) {
//        this(player, game, new MinMaxActionSelectionStrategy(game));
        this(player, game, new AlphaBetaActionSelectionStrategy(game));
    }

    BotActionInputObserver(Player player, Game game, ActionSelectionStrategy actionStrategy) {
        super(LOGGER, player, game);
        this.actionStrategy = actionStrategy;
    }

    @Override
    protected String getActionCommand() {
        var calculatedAction = this.actionStrategy.select(player.getColor());
        if (calculatedAction.isEmpty()) {
            return DEFEAT_COMMAND;
        }

        var action = calculatedAction.get();
        if (isPromote(action)) {
            this.promoteAction = (PiecePromoteAction<?,?>) action;
        }

        return adapt(action);
    }

    @Override
    protected String getPromotionPieceType() {
        if (this.promoteAction == null) {
            throw new IllegalStateException("Unknown promotion action");
        }

        return String.valueOf(this.promoteAction.getPieceType());
    }
}