package com.agutsul.chess.activity.action.formatter;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.substring;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Checkable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.activity.action.memento.ActionMementoDecorator;
import com.agutsul.chess.piece.Piece;

public enum AlgebraicActionFormatter implements ActionFormatter {
    MOVE_MODE(Action.Type.MOVE) {
        @Override
        public String formatMemento(ActionMemento<?,?> memento) {
            return String.format("%s%s%s",
                    memento.getPieceType(),
                    formatCode(memento.getCode()),
                    memento.getTarget()
            );
        }
    },
    CASTLING_MODE(Action.Type.CASTLING) {
        @Override
        public String formatMemento(ActionMemento<?,?> memento) {
            return memento.getCode();
        }
    },
    CAPTURE_MODE(Action.Type.CAPTURE) {
        @Override
        public String formatMemento(ActionMemento<?,?> memento) {
            var code = formatCode(memento.getCode());

            String source = null;
            if (Piece.Type.PAWN.equals(memento.getPieceType())) {
                source = formatPawnPosition(memento.getSource());
                code = Objects.equals(source, code) ? EMPTY : code;
            } else {
                source = String.valueOf(memento.getPieceType());
            }

            return String.format("%s%sx%s",
                    source,
                    code,
                    memento.getTarget()
            );
        }
    },
    EN_PASSANT_MODE(Action.Type.EN_PASSANT) {
        @Override
        public String formatMemento(ActionMemento<?,?> memento) {
            var originMemento = (ActionMemento<?,?>) memento.getSource();

            var source = formatPawnPosition(originMemento.getSource());
            var code = formatCode(memento.getCode());

            return String.format("%s%sx%s e.p.",
                    source,
                    Objects.equals(source, code) ? EMPTY : code,
                    memento.getTarget()
            );
        }
    },
    PROMOTE_MODE(Action.Type.PROMOTE) {
        @Override
        public String formatMemento(ActionMemento<?,?> memento) {
            var originMemento = (ActionMemento<?,?>) memento.getTarget();

            var originAction = memento.getCode() != null
                    ? format(new ActionMementoDecorator<>(originMemento, memento.getCode()))
                    : format(originMemento);

            return String.format("%s%s",
                    originAction,
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

    static String formatPawnPosition(Object pawnPosition) {
        return substring(String.valueOf(pawnPosition), 0, 1);
    }

    static String formatCode(String code) {
        return defaultIfNull(code, EMPTY);
    }

    public static String format(ActionMemento<?,?> memento) {
        var formatter = MODES.get(memento.getActionType());

        var builder = new StringBuilder();
        builder.append(formatter.formatMemento(memento));

        if (memento instanceof Checkable) {
            builder.append(((Checkable) memento).isCheckMated() ? "#" : "+");
        }

        return builder.toString();
    }
}