package com.agutsul.chess.game.pgn;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.game.Termination;

public enum PgnTermination implements Termination {
    ABANDONED("abandoned"),
    ADJUDICATION("adjudication"),
    DEATH("death"),
    EMERGENCY("emergency"),
    NORMAL("normal"),
    RULES_INFRACTION("rules infraction"),
    TIME_FORFEIT("time forfeit"),
    UNTERMINATED("unterminated");

    private static Map<String,PgnTermination> CODES = Stream.of(values())
            .collect(toMap(PgnTermination::label, identity()));

    private String label;

    PgnTermination(String label) {
        this.label = label;
    }

    String label() {
        return label;
    }

    public static PgnTermination codeOf(String label) {
        return CODES.get(lowerCase(label));
    }
}