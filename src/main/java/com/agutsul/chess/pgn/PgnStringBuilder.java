package com.agutsul.chess.pgn;

import static java.lang.System.lineSeparator;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.RegExUtils.removeAll;
import static org.apache.commons.lang3.StringUtils.containsAny;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.remove;

import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.Builder;

final class PgnStringBuilder
        implements Builder<String> {

    private static final String EVAL_PATTERN_TEMPLATE = "(%eval ){1}[-]{0,1}[#]{0,1}[-]{0,1}[\\d\\.]{1,}";
    private static final String DOTS_PATTERN_TEMPLATE = "([\\d]{1,}[\\.]{3}){1}";

    private final StringBuilder stringBuilder;
    private final Pattern evalPattern;
    private final Pattern dotsPattern;

    public PgnStringBuilder() {
        this.stringBuilder = new StringBuilder();

        this.evalPattern = compile(EVAL_PATTERN_TEMPLATE);
        this.dotsPattern = compile(DOTS_PATTERN_TEMPLATE);
    }

    public PgnStringBuilder append(String string) {
        this.stringBuilder.append(prepare(string));

        if (isNotBlank(string)) {
            this.stringBuilder.append(lineSeparator());
        }

        return this;
    }

    public void reset() {
        this.stringBuilder.setLength(0);
    }

    @Override
    public String build() {
        return this.stringBuilder.toString();
    }

    private String prepare(String string) {

        if (isEmpty(string)) {
            return string;
        }

        if (containsAny(string, "?!")) {
            string = remove(remove(string, "?"), "!");
        }

        //  check if string contains: ' { [%eval 0.53] }'
        var evalMatcher = evalPattern.matcher(string);
        if (evalMatcher.find()) {
            string = removeAll(string, evalPattern);
            string = remove(string, " { [] }");
        }

        //  check if string contains: '1...'
        var dotsMatcher = dotsPattern.matcher(string);
        if (dotsMatcher.find()) {
            string = removeAll(string, dotsPattern);
        }

        return string;
    }
}