package com.agutsul.chess.player.observer;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Random;

import org.slf4j.Logger;

import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.game.AbstractPlayableGame;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;

public final class RandomActionInputObserver
        extends AbstractPlayerInputObserver {

    private static final Logger LOGGER = getLogger(RandomActionInputObserver.class);

    private final Random random;

    public RandomActionInputObserver(Player player, Game game) {
        this(player, game, new Random());
    }

    RandomActionInputObserver(Player player, Game game, Random random) {
        super(LOGGER, player, game);
        this.random = random;
    }

    @Override
    protected String getActionCommand() {
        var board = ((AbstractPlayableGame) this.game).getBoard();

        var pieces  = board.getPieces(player.getColor());
        var actions = pieces.stream()
                .map(piece -> board.getActions(piece))
                .flatMap(Collection::stream)
                .toList();

        if (actions.isEmpty()) {
            return DEFEAT_COMMAND;
        }

        if (actions.size() == 1) {
            return adaptAction(actions.get(0));
        }

        var index = this.random.nextInt(0, actions.size());
        return adaptAction(actions.get(index));
    }

    @Override
    protected String getPromotionPieceType() {
        var value = this.random.nextDouble();
        if (value <= 0.25) {
            return Piece.Type.ROOK.code();
        } else if (value > 0.25 && value <= 0.5) {
            return Piece.Type.KNIGHT.code();
        } else if (value > 0.5  && value <= 0.75) {
            return Piece.Type.BISHOP.code();
        } else {
            return Piece.Type.QUEEN.code();
        }
    }

    private static String adaptAction(Action<?> action) {
        switch (action.getType()) {
        case Action.Type.PROMOTE:
            return adaptAction((Action<?>) action.getSource());
        case Action.Type.CASTLING:
            return adaptAction((Action<?>) action.getSource());
        default:
            return String.format("%s %s",
                    ((Positionable) action.getSource()).getPosition(), // source position
                    action.getPosition()                               // target position
            );
        }
    }
}