package com.agutsul.chess.pgn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.GameBuilder;
import com.agutsul.chess.game.Termination;
import com.agutsul.chess.game.pgn.PgnGame;
import com.agutsul.chess.game.state.BlackWinGameState;
import com.agutsul.chess.game.state.DefaultGameState;
import com.agutsul.chess.game.state.DrawnGameState;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.game.state.WhiteWinGameState;
import com.agutsul.chess.player.UserPlayer;

final class PgnGameBuilder
        implements GameBuilder {

    private final List<String> actions = new ArrayList<>();
    private final Map<String,String> tags = new HashMap<>();

    private String event;
    private String site;
    private String round;
    private String whitePlayer;
    private String blackPlayer;
    private String gameState;
    private String terminationType;

    @Override
    public PgnGame build() {
        var player1 = new UserPlayer(whitePlayer, Colors.WHITE);
        var player2 = new UserPlayer(blackPlayer, Colors.BLACK);

        var game = new PgnGame(player1, player2, tags, actions);

        game.setEvent(event);
        game.setSite(site);
        game.setRound(round);

        game.setParsedGameState(resolveState(gameState));
        game.setParsedTermination(Termination.codeOf(terminationType));

        return game;
    }

    GameBuilder withEvent(String event) {
        this.event = event;
        return this;
    }

    GameBuilder withSite(String site) {
        this.site = site;
        return this;
    }

    GameBuilder withRound(String round) {
        this.round = round;
        return this;
    }

    GameBuilder withWhitePlayer(String playerName) {
        this.whitePlayer = playerName;
        return this;
    }

    GameBuilder withBlackPlayer(String playerName) {
        this.blackPlayer = playerName;
        return this;
    }

    GameBuilder withGameState(String state) {
        this.gameState = state;
        return this;
    }

    GameBuilder withGameTermination(String terminationType) {
        this.terminationType = terminationType;
        return this;
    }

    GameBuilder withActions(List<String> actions) {
        this.actions.addAll(actions);
        return this;
    }

    GameBuilder addAction(String action) {
        this.actions.add(action);
        return this;
    }

    GameBuilder withTags(Map<String,String> tags) {
        this.tags.putAll(tags);
        return this;
    }

    GameBuilder addTag(String name, String value) {
        this.tags.put(name, value);
        return this;
    }

    private static GameState resolveState(String state) {
        switch (GameState.Type.codeOf(state)) {
        case WHITE_WIN:
            return new WhiteWinGameState();
        case BLACK_WIN:
            return new BlackWinGameState();
        case DRAWN_GAME:
            return new DrawnGameState();
        default:
            return new DefaultGameState();
        }
    }
}