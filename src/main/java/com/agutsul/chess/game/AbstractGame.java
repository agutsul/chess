package com.agutsul.chess.game;

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

abstract class AbstractGame
        implements Game {

    private static final Map<Color,GameState> WIN_STATES = Map.of(
            Colors.WHITE, new WhiteWinGameState(),
            Colors.BLACK, new BlackWinGameState()
    );

    protected final Logger logger;

    private final Map<Color,Player> players;

    protected String event;
    protected String site;
    protected String round;

    protected LocalDateTime startedAt;
    protected LocalDateTime finishedAt;

    AbstractGame(Logger logger, Player whitePlayer, Player blackPlayer) {
        this.logger = logger;
        this.players = Stream.of(whitePlayer, blackPlayer)
                .collect(toMap(Player::getColor, identity()));
    }

    @Override
    public final String getEvent() {
        return event;
    }

    @Override
    public final String getSite() {
        return site;
    }

    @Override
    public final String getRound() {
        return round;
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
        return players.get(color);
    }

    @Override
    public final LocalDateTime getStartedAt() {
        return startedAt;
    }

    @Override
    public final LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    @Override
    public final GameState getState() {
        if (Objects.isNull(getFinishedAt())) {
            return new DefaultGameState();
        }

        return Stream.ofNullable(getWinner())
                .flatMap(Optional::stream)
                .findFirst()
                .map(winner -> WIN_STATES.get(winner.getColor()))
                .orElse(new DrawnGameState());
    }
}