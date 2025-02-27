package com.agutsul.chess.game;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.Builder;

public interface GameBuilder
        extends Builder<Game> {

    GameBuilder withEvent(String event);
    GameBuilder withSite(String site);
    GameBuilder withRound(String round);

    GameBuilder withWhitePlayer(String playerName);
    GameBuilder withBlackPlayer(String playerName);

    GameBuilder withGameState(String state);
    GameBuilder withGameTermination(String termination);

    GameBuilder withActions(List<String> actions);
    GameBuilder addAction(String action);

    GameBuilder withTags(Map<String,String> tags);
    GameBuilder addTag(String name, String value);
}