package com.agutsul.chess.position;

import static com.agutsul.chess.position.Position.MAX;
import static com.agutsul.chess.position.Position.MIN;
import static com.agutsul.chess.position.Position.codeOf;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PositionFactory {
    INSTANCE;

    private Collection<String> CENTER_POSITION_CODES = List.of("d4","d5","e4","e5");

    private Map<String,Position> positions = new HashMap<>(MAX * MAX);

    PositionFactory() {
        for (int x = MIN; x < MAX; x++) {
            for (int y = MIN; y < MAX; y++) {
                var position = new PositionImpl(x, y);
                var code = position.getCode();

                positions.put(code, CENTER_POSITION_CODES.contains(code)
                        ? new CentralPosition(position)
                        : position
                );
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