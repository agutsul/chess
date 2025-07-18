package com.agutsul.chess.player.observer;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import com.agutsul.chess.activity.action.ActionAdapter;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.PlayerCommand;

public final class RandomActionInputObserver
        extends AbstractPlayerInputObserver
        implements ActionAdapter {

    private final Random random;

    public RandomActionInputObserver(Player player, Game game) {
        this(player, game, new Random());
    }

    RandomActionInputObserver(Player player, Game game, Random random) {
        super(player, game);
        this.random = random;
    }

    @Override
    protected String getActionCommand(Optional<Long> timeout) {
        var board = this.game.getBoard();

        var pieces  = board.getPieces(player.getColor());
        var actions = pieces.stream()
                .map(piece -> board.getActions(piece))
                .flatMap(Collection::stream)
                .toList();

        if (actions.isEmpty()) {
            return PlayerCommand.DEFEAT.code();
        }

        if (actions.size() == 1) {
            return adapt(actions.getFirst());
        }

        var index = this.random.nextInt(0, actions.size());
        return adapt(actions.get(index));
    }

    @Override
    protected String getPromotionPieceType(Optional<Long> timeout) {
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
}