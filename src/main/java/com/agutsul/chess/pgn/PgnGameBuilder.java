package com.agutsul.chess.pgn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.game.GameBuilderAdapter;
import com.agutsul.chess.game.pgn.PgnGame;
import com.agutsul.chess.game.state.BlackWinGameState;
import com.agutsul.chess.game.state.DefaultGameState;
import com.agutsul.chess.game.state.DrawnGameState;
import com.agutsul.chess.game.state.GameState;
import com.agutsul.chess.game.state.WhiteWinGameState;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.player.UserPlayer;

final class PgnGameBuilder
        implements GameBuilderAdapter {

    private final List<String> actions = new ArrayList<>();
    private final Map<String,String> tags = new HashMap<>();

    private Player whitePlayer;
    private Player blackPlayer;
    private GameState gameState;

    @Override
    public Game build() {
        var game = new PgnGame(whitePlayer, blackPlayer, tags, actions);
        game.setParsedGameState(gameState);
        return game;
    }

    @Override
    public GameBuilderAdapter withWhitePlayer(String playerName) {
        this.whitePlayer = new UserPlayer(playerName, Colors.WHITE);
        return this;
    }

    @Override
    public GameBuilderAdapter withBlackPlayer(String playerName) {
        this.blackPlayer = new UserPlayer(playerName, Colors.BLACK);
        return this;
    }

    @Override
    public GameBuilderAdapter withGameState(String state) {
        this.gameState = resolve(state);
        return this;
    }

    @Override
    public GameBuilderAdapter withActions(List<String> actions) {
        this.actions.addAll(actions);
        return this;
    }

    @Override
    public GameBuilderAdapter addAction(String action) {
        this.actions.add(action);
        return this;
    }

    @Override
    public GameBuilderAdapter withTags(Map<String,String> tags) {
        this.tags.putAll(tags);
        return this;
    }

    @Override
    public GameBuilderAdapter addTag(String name, String value) {
        this.tags.put(name, value);
        return this;
    }

    private static GameState resolve(String state) {
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