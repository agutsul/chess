package com.agutsul.chess.position.cache;

import com.agutsul.chess.Valuable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.position.Position;

public interface PositionCache<VP extends Position & Valuable<Integer>> {

    void refresh();

    VP get(Color color, Position position);
}