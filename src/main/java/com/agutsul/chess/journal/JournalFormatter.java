package com.agutsul.chess.journal;

import static java.lang.System.lineSeparator;

import com.agutsul.chess.action.formatter.AlgebraicActionFormatter;
import com.agutsul.chess.action.memento.ActionMemento;

class JournalFormatter {

    public static String format(Journal<? extends Memento> journal) {
        var builder = new StringBuilder();
        for (int i = 0, j = 1; i < journal.size(); i+=2, j++) {
            builder.append(j).append(".");
            builder.append(format(journal.get(i)));

            if (i + 1 < journal.size()) {
                builder.append(format(journal.get(i + 1)));
            }

            builder.append(lineSeparator());
        }

        return builder.toString();
    }

    private static String format(Memento memento) {
        return String.format("\t%s",
                AlgebraicActionFormatter.format((ActionMemento<?,?>) memento));
    }
}