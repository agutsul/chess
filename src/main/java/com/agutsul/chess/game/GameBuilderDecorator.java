package com.agutsul.chess.game;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.Builder;

public interface GameBuilderDecorator
        extends Builder<Game> {

    GameBuilderDecorator withWhitePlayer(String playerName);
    GameBuilderDecorator withBlackPlayer(String playerName);

    GameBuilderDecorator withGameState(String state);
    GameBuilderDecorator withGameTermination(String termination);

    GameBuilderDecorator withActions(List<String> actions);
    GameBuilderDecorator addAction(String action);

    GameBuilderDecorator withTags(Map<String,String> tags);
    GameBuilderDecorator addTag(String name, String value);
}