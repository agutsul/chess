package com.agutsul.chess.game;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.Builder;

public interface GameBuilderAdapter
        extends Builder<Game> {

    GameBuilderAdapter withWhitePlayer(String playerName);
    GameBuilderAdapter withBlackPlayer(String playerName);
    GameBuilderAdapter withGameState(String state);

    GameBuilderAdapter withActions(List<String> actions);
    GameBuilderAdapter addAction(String action);

    GameBuilderAdapter withTags(Map<String,String> tags);
    GameBuilderAdapter addTag(String name, String value);
}