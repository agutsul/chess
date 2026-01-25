package com.agutsul.chess.position;

import static com.agutsul.chess.position.Position.MAX;
import static com.agutsul.chess.position.Position.MIN;
import static com.agutsul.chess.position.Position.codeOf;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.util.HashMap;
import java.util.Map;

public enum PositionFactory {
    INSTANCE;

    private Map<String, Position> positions = new HashMap<>(MAX * MAX);

    PositionFactory() {
        for (int x = MIN; x < MAX; x++) {
            for (int y = MIN; y < MAX; y++) {
                var position = new PositionImpl(x, y);
                positions.put(position.getCode(), position);
            }
        }
    }

    public Position create(String code) {
        return positions.get(code);
    }

    public Position create(int x, int y) {
        var code = codeOf(x, y);
        return nonNull(code) ? create(code) : null;
    }

    public static Position positionOf(int x, int y) {
        return INSTANCE.create(x, y);
    }

    public static Position positionOf(String code) {
        return INSTANCE.create(lowerCase(code));
    }
}