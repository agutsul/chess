package com.agutsul.chess.journal;

import static java.lang.System.lineSeparator;

import com.agutsul.chess.action.formatter.AlgebraicActionFormatter;
import com.agutsul.chess.action.memento.ActionMemento;

class JournalFormatter {

    enum Type {
        SINGLE_LINE("\t"),
        MULTI_LINE(lineSeparator());

        private String separator;

        Type(String separator) {
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

    public static String format(Journal<? extends Memento> journal) {
        return format(journal, Type.MULTI_LINE);
    }

    public static String format(Journal<? extends Memento> journal, Type type) {
        var builder = new StringBuilder();
        for (int i = 0, j = 1; i < journal.size(); i+=2, j++) {
            builder.append(j).append(".");
            builder.append(format(journal.get(i)));

            if (i + 1 < journal.size()) {
                builder.append(format(journal.get(i + 1)));
            }

            builder.append(type.separator());
        }

        return builder.toString();
    }

    private static String format(Memento memento) {
        return String.format("\t%s",
                AlgebraicActionFormatter.format((ActionMemento<?,?>) memento));
    }
}