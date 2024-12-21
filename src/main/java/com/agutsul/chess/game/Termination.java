package com.agutsul.chess.game;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import java.util.Map;
import java.util.stream.Stream;

public enum Termination {
    ABANDONED("abandoned"),
    ADJUDICATION("adjudication"),
    DEATH("death"),
    EMERGENCY("emergency"),
    NORMAL("normal"),
    RULES_INFRACTION("rules infraction"),
    TIME_FORFEIT("time forfeit"),
    UNTERMINATED("unterminated");

    private static Map<String,Termination> CODES = Stream.of(values())
            .collect(toMap(Termination::label, identity()));

    private String label;

    Termination(String label) {
        this.label = label;
    }

    String label() {
        return label;
    }

    public static Termination codeOf(String label) {
        return CODES.get(lowerCase(label));
    }
}