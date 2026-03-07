package com.agutsul.chess.journal;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.Objects;

import com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.color.Colors;

public class JournalFormatter {

    public enum Mode {
        SINGLE_LINE(SPACE),
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
        for (int i = 0, j = 1; i < journal.size(); j++) {

            if (i != 0) {
                builder.append(mode.separator());
            }

            builder.append(j);

            var actionMemento = journal.get(i);
            if (i == 0) {
                builder.append(Objects.equals(Colors.WHITE, actionMemento.getColor()) ? "." : "...");
            } else {
                builder.append(".");
            }

            builder.append(SPACE);
            builder.append(format(actionMemento));

            if (i + 1 < journal.size() && Objects.equals(Colors.WHITE, actionMemento.getColor())) {
                builder.append(SPACE);
                builder.append(format(journal.get(i + 1)));

                i+=2;
            } else {
                i++;
            }
        }

        return builder.toString();
    }

    private static String format(ActionMemento<?,?> memento) {
        return StandardAlgebraicActionFormatter.format(memento);
    }
}