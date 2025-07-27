package com.agutsul.chess.game.pgn;

import static com.agutsul.chess.timeout.TimeoutFactory.createActionTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createGameTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createIncrementalTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createMixedTimeout;
import static com.agutsul.chess.timeout.TimeoutFactory.createUnknownTimeout;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.split;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;

import com.agutsul.chess.timeout.BaseTimeout;
import com.agutsul.chess.timeout.CompositeTimeout;
import com.agutsul.chess.timeout.Timeout;

public enum PgnTimeControl {
    UNKNOWN(Timeout.Type.UNKNOWN) {

        @Override
        protected Timeout parse(String ignoredText) {
            return createUnknownTimeout();
        }
    },
    NO_TIME_CONTROL(null) {

        @Override
        protected Timeout parse(String ignoredText) {
            return null;
        }
    },
    // "40/9000" - 40 actions in 9000 seconds => game timeout
    MIXED(Timeout.Type.ACTIONS_PER_PERIOD) {

        @Override
        protected Timeout parse(String text) {
            var strings = split(text, "/");
            return createMixedTimeout(toMilliseconds(strings[1]), parseInt(strings[0]));
        }
    },
    // "300" - 300 seconds for game => game timeout ( blitz )
    GENERIC(Timeout.Type.GENERIC) {

        @Override
        protected Timeout parse(String text) {
            return createGameTimeout(toMilliseconds(text));
        }
    },
    // "4500+60" - min number in seconds per action plus extra seconds after each action
    INCREMENTAL(Timeout.Type.INCREMENTAL) {

        @Override
        protected Timeout parse(String text) {
            var strings = split(text, "+");

            // potentially it can be either "*180" or "40/9000" or "300"
            // ( SANDCLOCK or ACTIONS_PER_PERIOD or GENERAL )
            var incrementalTimeout = Stream.of(strings[0])
                    .map(PgnTimeControl::timeoutOf)
                    .filter(Objects::nonNull)                          // skip NO_TIME_CONTROL
                    .filter(timeout -> timeout instanceof BaseTimeout) // skip UNKNOWN & nested incremental
                    .map(timeout -> (BaseTimeout) timeout)
                    .map(timeout -> createIncrementalTimeout(timeout, toMilliseconds(strings[1])))
                    .findFirst()
                    .orElse(null);

            return (Timeout) incrementalTimeout;
        }
    },
    // "*180" - min number in seconds for each player => action timeout
    ACTION(Timeout.Type.ACTIONS_PER_PERIOD) {

        @Override
        protected Timeout parse(String text) {
            return createActionTimeout(toMilliseconds(text.substring(1)));
        }
    };

    private Timeout.Type type;

    PgnTimeControl(Timeout.Type type) {
        this.type = type;
    }

    public Optional<Timeout.Type> type() {
        return Optional.ofNullable(type);
    }

    protected abstract Timeout parse(String text);

    public static Timeout timeoutOf(String text) {
        if (isBlank(text)) {
            return null;
        }

        var timeouts = Stream.of(split(text, ":"))
                .map(PgnTimeControl::toTimeout)
                .filter(Objects::nonNull)
                .toList();

        var timeout = switch (timeouts.size()) {
        case 0  -> null;
        case 1  -> timeouts.getFirst();
        default -> new CompositeTimeout(timeouts);
        };

        return timeout;
    }

    private static Timeout toTimeout(String timeControl) {
        var timeout = switch (timeControl) {
        case String str when "?".equals(str) -> UNKNOWN.parse(str);
        case String str when "-".equals(str) -> NO_TIME_CONTROL.parse(str);
        case String str when isNumeric(str)  -> GENERIC.parse(str);
        case String str when containsIn(str,  "+") -> INCREMENTAL.parse(str);
        case String str when isAction(str, "*") -> ACTION.parse(str);
        case String str when isMixed(str,  "/") -> MIXED.parse(str);
        default -> null;
        };

        return timeout;
    }

    private static boolean isMixed(String str, String searched) {
        return containsIn(str, searched)
                && isNumeric(str.replace(searched, EMPTY));
    }

    private static boolean isAction(String str, String searched) {
        return containsOnce(str, searched)
                && Strings.CI.startsWith(str, searched)
                && isNumeric(str.substring(1));
    }

    private static boolean containsIn(String str, String searched) {
        return containsOnce(str, searched)
                && !Strings.CI.startsWith(str, searched)
                && !Strings.CI.endsWith(str, searched);
    }

    private static boolean containsOnce(String str, String searched) {
        return countMatches(str, searched) == 1;
    }

    private static long toMilliseconds(String seconds) {
        return Duration.ofSeconds(parseLong(seconds)).toMillis();
    }
}