package com.agutsul.chess.activity.action.formatter;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.activity.action.memento.ActionMementoDecorator;
import com.agutsul.chess.piece.Piece;

public enum StandardAlgebraicActionFormatter implements ActionFormatter {
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

        private static final Map<Castlingable.Side,String> CODES = new EnumMap<>(Map.of(
                    Castlingable.Side.KING,  CASTLING_KING_SIDE_CODE,
                    Castlingable.Side.QUEEN, CASTLING_QUEEN_SIDE_CODE
        ));

        @Override
        public String formatMemento(ActionMemento<?,?> memento) {
            return CODES.get(Castlingable.Side.valueOf(memento.getCode()));
        }
    },
    CAPTURE_MODE(Action.Type.CAPTURE) {
        @Override
        public String formatMemento(ActionMemento<?,?> memento) {
            return String.format("%s%s%s",
                    formatSource(memento),
                    CAPTURE_CODE,
                    memento.getTarget()
            );
        }

        private static String formatSource(ActionMemento<?,?> memento) {
            if (Piece.Type.PAWN.equals(memento.getPieceType())) {
                return formatPawn(memento);
            }

            return String.format("%s%s",
                    memento.getPieceType(),
                    formatCode(memento.getCode())
            );
        }
    },
    EN_PASSANT_MODE(Action.Type.EN_PASSANT) {
        @Override
        public String formatMemento(ActionMemento<?,?> memento) {
            return String.format("%s%s%s",
                    formatPawn((ActionMemento<?,?>) memento.getSource()),
                    CAPTURE_CODE,
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

            return String.format("%s%s%s",
                    originAction,
                    PROMOTE_CODE,
                    memento.getPieceType()
            );
        }
    };

    public static final String GOOD_MOVE_CODE = "!";
    public static final String BAD_MOVE_CODE  = "?";
    public static final String CAPTURE_CODE   = "x";
    public static final String PROMOTE_CODE   = "=";
    public static final String CHECK_CODE     = "+";
    public static final String CHECKMATE_CODE = "#";
    public static final String CASTLING_KING_SIDE_CODE  = "O-O";
    public static final String CASTLING_QUEEN_SIDE_CODE = "O-O-O";

    private static final Map<Action.Type, StandardAlgebraicActionFormatter> MODES =
            Stream.of(values()).collect(toMap(StandardAlgebraicActionFormatter::type, identity()));

    private Action.Type type;

    StandardAlgebraicActionFormatter(Action.Type type) {
        this.type = type;
    }

    private Action.Type type() {
        return type;
    }

    private static String formatPawn(ActionMemento<?,?> memento) {
        var position = String.valueOf(memento.getSource());
        var label = String.valueOf(position.charAt(0));

        var code = formatCode(memento.getCode());
        return String.format("%s%s",
                label,
                Objects.equals(label, code) ? EMPTY : code
        );
    }

    private static String formatCode(String code) {
        return defaultIfNull(code, EMPTY);
    }

    public static String format(ActionMemento<?,?> memento) {
        var formatter = MODES.get(memento.getActionType());

        var builder = new StringBuilder();
        builder.append(formatter.formatMemento(memento));

        if (memento instanceof Checkable) {
            builder.append(((Checkable) memento).isCheckMated()
                    ? CHECKMATE_CODE
                    : CHECK_CODE
            );
        }

        return builder.toString();
    }
}