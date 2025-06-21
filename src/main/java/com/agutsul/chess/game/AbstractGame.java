package com.agutsul.chess.game;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.state.BlackWinGameState;
import com.agutsul.chess.game.state.DefaultGameState;
import com.agutsul.chess.game.state.DrawnGameState;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.game.state.WhiteWinGameState;
import com.agutsul.chess.player.Player;

public abstract class AbstractGame
        implements Game {

    private static final Map<Color,GameState> WIN_STATES = Map.of(
            Colors.WHITE, new WhiteWinGameState(),
            Colors.BLACK, new BlackWinGameState()
    );

    private final Map<Color,Player> players;

    protected final Logger logger;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private Player currentPlayer;
    private Player winnerPlayer;

    AbstractGame(Logger logger, Player whitePlayer, Player blackPlayer) {
        this.logger = logger;
        this.players = Stream.of(whitePlayer, blackPlayer)
                .collect(toMap(Player::getColor, identity()));
    }

    @Override
    public final Player getWhitePlayer() {
        return getPlayer(Colors.WHITE);
    }

    @Override
    public final Player getBlackPlayer() {
        return getPlayer(Colors.BLACK);
    }

    @Override
    public final Player getPlayer(Color color) {
        return this.players.get(color);
    }

    public final void setStartedAt(LocalDateTime dateTime) {
        this.startedAt = dateTime;
    }

    @Override
    public final LocalDateTime getStartedAt() {
        return this.startedAt;
    }

    public final void setFinishedAt(LocalDateTime dateTime) {
        this.finishedAt = dateTime;
    }

    @Override
    public final LocalDateTime getFinishedAt() {
        return this.finishedAt;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    @Override
    public final Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public final Player getOpponentPlayer() {
        return Objects.equals(getCurrentPlayer(), getWhitePlayer())
                ? getBlackPlayer()
                : getWhitePlayer();
    }

    @Override
    public final Optional<Player> getWinnerPlayer() {
        return Optional.ofNullable(this.winnerPlayer);
    }

    public void setWinnerPlayer(Player player) {
        this.winnerPlayer = player;
    }

    @Override
    public final GameState getState() {
        if (isNull(getFinishedAt())) {
            return new DefaultGameState();
        }

        var state = Stream.of(getWinnerPlayer())
                .flatMap(Optional::stream)
                .findFirst()
                .map(Player::getColor)
                .map(winnerColor -> WIN_STATES.get(winnerColor))
                .orElse(new DrawnGameState());

        return state;
    }
}