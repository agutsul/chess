package com.agutsul.chess.ai;

import java.util.Optional;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.color.Color;

public interface ActionSelectionStrategy {
    Optional<Action<?>> select(Color color);
}