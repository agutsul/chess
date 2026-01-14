package com.agutsul.chess.game.fen;

import static com.agutsul.chess.activity.action.Action.isBigMove;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.upperCase;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.activity.action.memento.ActionMemento;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.color.Colors;
import com.agutsul.chess.game.Game;
import com.agutsul.chess.journal.Journal;
import com.agutsul.chess.journal.statistic.JournalActionCalculation;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.player.Player;
import com.agutsul.chess.position.Position;

public class FenGameFormatter {

    private static final String FEN_PATTERN = "%s %s %s %s %d %d";
    private static final String LINE_SEPARATOR = "/";
    private static final String DISABLE_ALL_SYMBOL = "-";

    public static String format(Game game) {
        var formattedEnPassant = formatEnPassant(game.getBoard(), game.getJournal(), game.getOpponentPlayer());
        return String.format(FEN_PATTERN,
                formatBoard(game.getBoard()),
                formatPlayer(game.getCurrentPlayer()),
                formatCastlings(game.getBoard()),
                formattedEnPassant,
                halfMoves(game),
                !DISABLE_ALL_SYMBOL.equals(formattedEnPassant)
                        ? fullMoves(game.getJournal(), game.getOpponentPlayer())
                        : fullMoves(game.getJournal())
        );
    }

    private static int fullMoves(Journal<ActionMemento<?,?>> journal, Player player) {
        return Colors.BLACK.equals(player.getColor())
                ? fullMoves(journal) - 1
                : fullMoves(journal);
    }

    private static int fullMoves(Journal<ActionMemento<?,?>> journal) {
        return journal.size(Colors.BLACK);
    }

    private static int halfMoves(Game game) {
        var calculationTask = new JournalActionCalculation(game.getBoard(), game.getJournal());
        var results = Math.max(
                calculationTask.calculate(game.getCurrentPlayer().getColor(), 50),
                0
        );

        return game instanceof FenGame
                ? ((FenGame<?>) game).getParsedHalfMoves() + results
                : results;
    }

    private static String formatEnPassant(Board board, Journal<ActionMemento<?,?>> journal,
                                          Player player) {

        var actions = journal.get(player.getColor());
        if (actions.isEmpty()) {
            return DISABLE_ALL_SYMBOL;
        }

        var lastAction = actions.getLast();
        if (!isBigMove(lastAction.getActionType())) {
            return DISABLE_ALL_SYMBOL;
        }

        @SuppressWarnings("unchecked")
        var formatted = Stream.of(lastAction)
                .map(action -> (ActionMemento<String,String>) action)
                .map(ActionMemento::getTarget)
                .map(code -> board.getPosition(code))
                .flatMap(Optional::stream)
                .flatMap(targetPosition -> Stream.of(board.getPiece(targetPosition))
                        .flatMap(Optional::stream)
                        .filter(Piece::isPawn)
                        // find position 'behind' moved pawn
                        .map(pawn -> board.getPosition(
                                targetPosition.x(),
                                targetPosition.y() - pawn.getDirection()
                        ))
                )
                .flatMap(Optional::stream)
                .map(String::valueOf)
                .findFirst()
                .orElse(DISABLE_ALL_SYMBOL);

        return formatted;
    }

    private static String formatCastlings(Board board) {
        var whiteSides = formatCastlingSides(board, Colors.WHITE);
        var blackSides = formatCastlingSides(board, Colors.BLACK);

        var sides = String.format("%s%s", upperCase(whiteSides), lowerCase(blackSides));
        return isBlank(sides)
                ? DISABLE_ALL_SYMBOL
                : sides;
    }

    private static String formatCastlingSides(Board board, Color color) {
        return Stream.of(board.getKing(color))
                .flatMap(Optional::stream)
                .map(KingPiece::getSides)
                .flatMap(Collection::stream)
                .sorted()
                .map(Castlingable.Side::name)
                .map(name -> name.charAt(0))
                .map(String::valueOf)
                .collect(joining(EMPTY));
    }

    private static String formatPlayer(Player player) {
        var color = String.valueOf(player.getColor());
        return lowerCase(String.valueOf(color.charAt(0)));
    }

    private static String formatBoard(Board board) {
        var builder = new StringBuilder();
        for (int j = Position.MAX - 1, k = 0; j >= Position.MIN; j--) {
            for (int i = Position.MIN; i < Position.MAX; i++) {
                var position = board.getPosition(i, j);
                if (position.isEmpty()) {
                    throw new IllegalArgumentException(String.format(
                            "Unsupported position: [%d,%d]",
                            i, j
                    ));
                }

                var piece = board.getPiece(position.get());
                if (piece.isPresent()) {
                    if (k > 0) {
                        builder.append(k);
                        k = 0;
                    }

                    builder.append(formatPiece(piece.get()));
                } else {
                    k++;
                }
            }

            if (k > 0) {
                builder.append(k);
                k = 0;
            }

            if (j > Position.MIN) {
                builder.append(LINE_SEPARATOR);
            }
        }

        return builder.toString();
    }

    private static String formatPiece(Piece<?> piece) {
        var pieceType = resolvePieceType(piece.getType());
        return Colors.WHITE.equals(piece.getColor())
                ? upperCase(pieceType)
                : lowerCase(pieceType);
    }

    private static String resolvePieceType(Piece.Type pieceType) {
        return switch (pieceType) {
        case BISHOP -> "b";
        case KNIGHT -> "n";
        case QUEEN  -> "q";
        case KING   -> "k";
        case ROOK   -> "r";
        case PAWN   -> "p";
        };
    }
}