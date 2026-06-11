package com.agutsul.chess.position;

import static com.agutsul.chess.position.Position.MAX;
import static com.agutsul.chess.position.Position.MIN;
import static com.agutsul.chess.position.Position.codeOf;
import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public enum PositionFactory {
    INSTANCE;

    private Collection<String> centerPositionCodes = Set.of("d4","d5","e4","e5");

    private Map<String,Position> positions = range(MIN, MAX)
            .mapToObj(x -> range(MIN, MAX)
                    .mapToObj(y -> createPosition(x, y))
            )
            .flatMap(identity())
            .collect(toMap(Position::getCode, identity()));

    public Position create(String code) {
        return positions.get(code);
    }

    public Position create(int x, int y) {
        var code = codeOf(x, y);
        return nonNull(code) ? create(code) : null;
    }

    private Position createPosition(int x, int y) {
        var position = new PositionImpl(x, y);
        return centerPositionCodes.contains(position.getCode())
                ? new CentralPosition(position)
                : position;
    }

    public static Position positionOf(int x, int y) {
        return INSTANCE.create(x, y);
    }

    public static Position positionOf(String code) {
        return INSTANCE.create(lowerCase(code));
    }
}