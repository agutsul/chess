package com.agutsul.chess.game;
import static com.agutsul.chess.color.Colors.isWhite;
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
import com.agutsul.chess.game.result.BlackWinGameResult;
import com.agutsul.chess.game.result.DefaultGameResult;
import com.agutsul.chess.game.result.DrawnGameResult;
import com.agutsul.chess.game.result.GameResult;
import com.agutsul.chess.game.result.WhiteWinGameResult;
import com.agutsul.chess.player.Player;

public abstract class AbstractGame
        implements Game {

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

    @Override
    public final LocalDateTime getStartedAt() {
        return this.startedAt;
    }

    @Override
    public final LocalDateTime getFinishedAt() {
        return this.finishedAt;
    }

    @Override
    public final GameResult getResult() {
        if (isNull(getFinishedAt())) {
            return new DefaultGameResult();
        }

        var result = Stream.of(getWinnerPlayer())
                .flatMap(Optional::stream)
                .findFirst()
                .map(player -> (GameResult) (isWhite(player.getColor())
                            ? new WhiteWinGameResult()
                            : new BlackWinGameResult()
                ))
                .orElse(new DrawnGameResult());

        return result;
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

    public final void setStartedAt(LocalDateTime dateTime) {
        this.startedAt = dateTime;
    }

    public final void setFinishedAt(LocalDateTime dateTime) {
        this.finishedAt = dateTime;
    }

    protected void setWinnerPlayer(Player player) {
        this.winnerPlayer = player;
    }

    protected void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }
}