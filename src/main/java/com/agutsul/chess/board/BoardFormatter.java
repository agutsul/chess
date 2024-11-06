package com.agutsul.chess.board;

import static com.agutsul.chess.position.PositionFactory.positionOf;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.Optional;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

class BoardFormatter {

    private static final String ROW_SEPARATOR = "--+---+---+---+---+---+---+---+---+--";
    private static final String COLUMN_SEPARATOR = "|";

    public static String format(Board board) {
        var builder = new StringBuilder();

        // build header
        builder.append(formatLabelLine());
        builder.append(ROW_SEPARATOR).append(lineSeparator());

        // build board
        for (int y = Position.MAX - 1; y >= Position.MIN; y--) {
            builder.append(y + 1).append(formatColumnSeparator());

            for (int x = Position.MIN; x < Position.MAX; x++) {
                builder.append(formatPiece(board.getPiece(positionOf(x, y))));
                builder.append(formatColumnSeparator());

                if (x == Position.MAX - 1) {
                    builder.append(y + 1).append(lineSeparator());
                }
            }

            builder.append(ROW_SEPARATOR).append(lineSeparator());
        }

        // build footer
        builder.append(formatLabelLine()).append(lineSeparator());
        return builder.toString();
    }

    private static String formatPiece(Optional<Piece<Color>> piece) {
        return piece.isPresent() ? piece.get().getUnicode() : SPACE;
    }

    private static String formatLabelLine() {
        var builder = new StringBuilder();
        builder.append(String.format("  %s ", COLUMN_SEPARATOR));

        for (int i = Position.MIN; i < Position.MAX; i++) {
            builder.append(Position.LABELS[i]);
            builder.append(formatColumnSeparator());
        }

        builder.append(lineSeparator());
        return builder.toString();
    }

    private static String formatColumnSeparator() {
        return String.format(" %s ", COLUMN_SEPARATOR);
    }
}