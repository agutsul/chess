package com.agutsul.chess.fen;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.join;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class FenAntlrErrorListener extends BaseErrorListener {

    private final List<String> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        this.errors.add(String.format("line '%s': %d - %s", line, charPositionInLine, msg));
    }

    public int getErrorsCount() {
        return this.errors.size();
    }

    public boolean hasAnyErrors() {
        return !this.errors.isEmpty();
    }

    /**
     * Gets all errors (one per line)
     *
     * @return all errors (one per line), an empty string ("") if there are no
     *         errors
     */
    public String getErrors() {
        return join(this.errors, lineSeparator());
    }
}