package com.agutsul.chess.action.formatter;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.substring;

import java.util.Map;
import java.util.stream.Stream;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.memento.ActionMemento;
import com.agutsul.chess.action.memento.CastlingActionMemento;
import com.agutsul.chess.piece.Checkable;
import com.agutsul.chess.piece.Piece;

public enum AlgebraicActionFormatter {
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
                    ? formatPawnPosition(memento.getSource())
                    : memento.getPieceType();

            return String.format("%sx%s", source, memento.getTarget());
        }
    },
    EN_PASSANT_MODE(Action.Type.EN_PASSANT) {
        @Override
        String toString(ActionMemento<?,?> memento) {
            return String.format("%sx%s e.p.",
                    formatPawnPosition(((ActionMemento<?,?>) memento.getSource()).getSource()),
                    memento.getTarget()
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

    private static final Map<Action.Type, AlgebraicActionFormatter> MODES =
            Stream.of(values()).collect(toMap(AlgebraicActionFormatter::type, identity()));

    private Action.Type type;

    AlgebraicActionFormatter(Action.Type type) {
        this.type = type;
    }

    Action.Type type() {
        return type;
    }

    abstract String toString(ActionMemento<?,?> memento);

    static String formatPawnPosition(Object pawnPosition) {
        return substring(String.valueOf(pawnPosition), 0, 1);
    }

    public static String format(ActionMemento<?,?> memento) {
        var builder = new StringBuilder();
        builder.append(MODES.get(memento.getActionType()).toString(memento));

        if (memento instanceof Checkable) {
            builder.append(((Checkable) memento).isCheckMated() ? "#" : "+");
        }

        return builder.toString();
    }
}