package com.agutsul.chess.game.pgn;

import static com.agutsul.chess.game.pgn.PgnTermination.TIME_FORFEIT;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.antlr.pgn.action.PawnPromotionTypeAdapter;
import com.agutsul.chess.antlr.pgn.action.PgnActionAdapter;
import com.agutsul.chess.antlr.pgn.action.PieceActionAdapter;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.AbstractTimeoutException;
import com.agutsul.chess.exception.ActionTimeoutException;
import com.agutsul.chess.exception.GameTimeoutException;
import com.agutsul.chess.iterator.CurrentIterator;
import com.agutsul.chess.iterator.CurrentIteratorImpl;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.PlayerCommand;
import com.agutsul.chess.player.observer.AbstractPlayerInputObserver;

final class PgnPlayerInputObserver
        extends AbstractPlayerInputObserver {

    private final CurrentIterator<String> actionIterator;

    private final PgnActionAdapter pieceActionAdapter;
    private final PgnActionAdapter promotionTypeAdapter;

    PgnPlayerInputObserver(Player player, PgnGame<?> game, List<String> actions) {
        super(player, game);

        this.actionIterator = new CurrentIteratorImpl<>(actions.iterator());
        this.pieceActionAdapter = new PieceActionAdapter(game.getBoard(), player.getColor());
        this.promotionTypeAdapter = new PawnPromotionTypeAdapter(game.getBoard(), player.getColor());
    }

    @Override
    protected String getActionCommand(Optional<Long> timeout) {
        if (!actionIterator.hasNext()) {
            return finalCommand();
        }

        var action = actionIterator.next();
        return pieceActionAdapter.adapt(action);
    }

    @Override
    protected String getPromotionPieceType(Optional<Long> timeout) {
        if (!actionIterator.hasCurrent()) {
            return null;
        }

        var action = actionIterator.current();
        return promotionTypeAdapter.adapt(action);
    }

    private String finalCommand() {
        var pgnGame = (PgnGame<?>) this.game;

        var gameState  = pgnGame.getParsedGameState();
        var winnerType = gameState.getType();

        if (TIME_FORFEIT.equals(pgnGame.getParsedTermination())) {
            throw createTimeoutException(winnerType.color());
        }

        var command = switch (winnerType) {
        case WHITE_WIN, BLACK_WIN -> finalCommand(this.player, winnerType.color().get());
        case DRAWN_GAME -> PlayerCommand.DRAW;
        default -> PlayerCommand.EXIT;
        };

        return command.code();
    }

    private AbstractTimeoutException createTimeoutException(Optional<Color> winnerColor) {
        var contextTimeout = this.game.getContext().getTimeout();
        if (contextTimeout.isEmpty()) {
            throw new IllegalStateException("Timeout configuration missed");
        }

        // TODO: create exception based on Timeout.Type
        var timeoutException = Stream.of(winnerColor)
                .flatMap(Optional::stream)
                .filter(color -> !Objects.equals(this.player.getColor(), color))
                .map(color -> new GameTimeoutException(this.player))
                .map(exception -> (AbstractTimeoutException) exception)
                .findFirst()
                .orElse(new ActionTimeoutException(this.player));

        return timeoutException;
    }

    private static PlayerCommand finalCommand(Player player, Color color) {
        return Objects.equals(player.getColor(), color)
                ? PlayerCommand.WIN
                : PlayerCommand.DEFEAT;
    }
}