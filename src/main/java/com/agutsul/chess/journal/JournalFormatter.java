package com.agutsul.chess.journal;

import static java.lang.System.lineSeparator;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.substring;

import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.action.memento.CastlingActionMemento;
import com.agutsul.chess.action.memento.CheckMatedActionMemento;
import com.agutsul.chess.action.memento.CheckedActionMemento;
import com.agutsul.chess.piece.Piece;

class JournalFormatter {

    public static String format(Journal<? extends Memento> journal) {
        var builder = new StringBuilder();
        for (int i = 0, j = 1; i < journal.size(); i+=2, j++) {
            builder.append(j).append(".");
            builder.append(format((ActionMemento<?,?>) journal.get(i)));

            if (i + 1 < journal.size()) {
                builder.append(format((ActionMemento<?,?>) journal.get(i + 1)));
            }

            builder.append(lineSeparator());
        }

        return builder.toString();
    }

    private static String format(ActionMemento<?,?> memento) {
        var builder = new StringBuilder();
        builder.append("\t");
        builder.append(AlgebraicFormatter.format(memento));

        if (memento instanceof CheckMatedActionMemento) {
            builder.append("#");
        } else if (memento instanceof CheckedActionMemento) {
            builder.append("+");
        }

        return builder.toString();
    }

    private enum AlgebraicFormatter {
        MOVE_MODE(Action.Type.MOVE) {
            @Override
            String toString(ActionMemento<?,?> memento) {
                return String.format("%s%s", memento.getPieceType(), memento.getTarget());
            }
        },
        CASTLING_MODE(Action.Type.CASTLING) {
            @Override
            String toString(ActionMemento<?,?> memento) {
                return ((CastlingActionMemento) memento).getCode();
            }
        },
        CAPTURE_MODE(Action.Type.CAPTURE) {
            @Override
            String toString(ActionMemento<?,?> memento) {
                var source = Piece.Type.PAWN.equals(memento.getPieceType())
                        ? substring(String.valueOf(memento.getSource()), 0, 1)
                        : memento.getPieceType();

                return String.format("%sx%s", source, memento.getTarget());
            }
        },
        EN_PASSANT_MODE(Action.Type.EN_PASSANT) {
            @Override
            String toString(ActionMemento<?,?> memento) {
                return String.format("%sx%s e.p.",
                        substring(String.valueOf(memento.getSource()), 0, 1),
                        ((ActionMemento<?,?>) memento.getTarget()).getTarget()
                );
            }
        },
        PROMOTE_MODE(Action.Type.PROMOTE) {
            @Override
            String toString(ActionMemento<?,?> memento) {
                return String.format("%s%s",
                        ((ActionMemento<?,?>) memento.getTarget()).getTarget(),
                        memento.getPieceType().code()
                );
            }
        };

        private static final Map<Action.Type, AlgebraicFormatter> MODES =
                Stream.of(values()).collect(toMap(AlgebraicFormatter::type, identity()));

        private Action.Type type;

        AlgebraicFormatter(Action.Type type) {
            this.type = type;
        }

        Action.Type type() {
            return type;
        }

        abstract String toString(ActionMemento<?,?> memento);

        static String format(ActionMemento<?,?> memento) {
            return MODES.get(memento.getActionType()).toString(memento);
        }
    }
}