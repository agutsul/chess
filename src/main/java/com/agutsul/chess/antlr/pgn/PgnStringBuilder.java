package com.agutsul.chess.antlr.pgn;

import static java.lang.System.lineSeparator;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.RegExUtils.replaceAll;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.util.regex.Pattern;

import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.builder.Builder;

public final class PgnStringBuilder
        implements Builder<String> {

    private static final String EVAL_PATTERN_TEMPLATE = "(%eval ){1}[-]{0,1}[#]{0,1}[-]{0,1}[\\d\\.]{1,}";
    private static final String DOTS_PATTERN_TEMPLATE = "([\\d]{1,}[\\.]{3}){1}";

    private final StringBuilder stringBuilder;
    private final Pattern evalPattern;
    private final Pattern dotsPattern;

    private boolean isReady;

    public PgnStringBuilder() {
        this.stringBuilder = new StringBuilder();

        this.evalPattern = compile(EVAL_PATTERN_TEMPLATE);
        this.dotsPattern = compile(DOTS_PATTERN_TEMPLATE);
    }

    public PgnStringBuilder append(String string) {
        this.isReady = isNotBlank(string) && isNumeric(string.substring(0,1));
        this.stringBuilder.append(prepare(string));

        if (isNotBlank(string)) {
            this.stringBuilder.append(lineSeparator());
        }

        return this;
    }

    public boolean isReady() {
        return this.isReady;
    }

    public void reset() {
        this.stringBuilder.setLength(0);
        this.isReady = false;
    }

    @Override
    public String build() {
        return this.stringBuilder.toString();
    }

    private String prepare(String string) {

        if (isEmpty(string)) {
            return string;
        }

        if (Strings.CI.containsAny(string, "?!")) {
            string = Strings.CI.remove(Strings.CI.remove(string, "?"), "!");
        }

        //  check if string contains: ' { [%eval 0.53] }'
        var evalMatcher = evalPattern.matcher(string);
        if (evalMatcher.find()) {
            string = replaceAll((CharSequence) string, evalPattern, EMPTY);
            string = Strings.CI.remove(string, " { [] }");
        }

        //  check if string contains: '1...'
        var dotsMatcher = dotsPattern.matcher(string);
        if (dotsMatcher.find()) {
            string = replaceAll((CharSequence) string, dotsPattern, EMPTY);
        }

        return string;
    }
}