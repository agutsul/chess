package com.agutsul.chess.position;

import java.util.HashMap;
import java.util.Map;

public enum PositionFactory {
    INSTANCE;

    private Map<String, Position> positions = new HashMap<>();

    private PositionFactory() {
        for (int x = Position.MIN; x < Position.MAX; x++) {
            for (int y = Position.MIN; y < Position.MAX; y++) {
                var position = new PositionImpl(x, y);
                positions.put(position.getCode(), position);
            }
        }
    }

    public Position createPosition(String code) {
        return positions.get(code);
    }

    public Position createPosition(int x, int y) {
        return positions.values().stream()
                .filter(position -> position.x() == x && position.y() == y)
                .findFirst()
                .orElse(null);
    }
}
