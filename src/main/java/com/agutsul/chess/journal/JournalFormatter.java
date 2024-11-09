package com.agutsul.chess.journal;

import static java.lang.System.lineSeparator;

import com.agutsul.chess.action.formatter.AlgebraicActionFormatter;
import com.agutsul.chess.action.memento.ActionMemento;

public class JournalFormatter {

    public enum Mode {
        SINGLE_LINE("\t"),
        MULTI_LINE(lineSeparator());

        private String separator;

        Mode(String separator) {
            this.separator = separator;
        }

        String separator() {
            return separator;
        }

        @Override
        public String toString() {
            return name();
        }
    }

    public static String format(Journal<ActionMemento<?,?>> journal) {
        return format(journal, Mode.SINGLE_LINE);
    }

    public static String format(Journal<ActionMemento<?,?>> journal, Mode mode) {
        var builder = new StringBuilder();
        for (int i = 0, j = 1; i < journal.size(); i+=2, j++) {

            if (i != 0) {
                builder.append(mode.separator());
            }

            builder.append(j).append(". ");
            builder.append(format(journal.get(i)));

            if (i + 1 < journal.size()) {
                builder.append("\t");
                builder.append(format(journal.get(i + 1)));
            }
        }

        return builder.toString();
    }

    private static String format(ActionMemento<?,?> memento) {
        return AlgebraicActionFormatter.format(memento);
    }
}