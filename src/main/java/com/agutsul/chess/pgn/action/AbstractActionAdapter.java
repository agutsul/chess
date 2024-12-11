package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.Position.codeOf;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.remove;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.action.PieceCaptureAction;
import com.agutsul.chess.action.PieceMoveAction;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

abstract class AbstractActionAdapter
        implements ActionAdapter {

    final Board board;
    final Color color;

    AbstractActionAdapter(Board board, Color color) {
        this.board = board;
        this.color = color;
    }

    Optional<Piece<Color>> getMovablePiece(Piece.Type pieceType, String position) {
        return getMovablePiece(pieceType, null, position);
    }

    Optional<Piece<Color>> getMovablePiece(Piece.Type pieceType, String code, String position) {
        return getPiece(pieceType, code, position, PieceMoveAction.class);
    }

    Optional<Piece<Color>> getCapturablePiece(Piece.Type pieceType, String code, String position) {
        return getPiece(pieceType, code, position, PieceCaptureAction.class);
    }

    Collection<Piece<Color>> findPieces(Piece.Type pieceType, String code) {
        var pieces = board.getPieces(color, pieceType).stream()
                .filter(piece -> code == null || contains(codeOf(piece.getPosition()), code))
                .toList();

        return pieces;
    }

    boolean containsAction(Piece<Color> piece, String position, Class<?> actionClass) {
        @SuppressWarnings("unchecked")
        var actions = board.getActions(piece, (Class<? extends Action<?>>) actionClass);
        var actionExists = actions.stream()
                .anyMatch(action -> Objects.equals(codeOf(action.getPosition()), position));

        return actionExists;
    }

    Optional<Piece<Color>> getPiece(Piece.Type pieceType, String code,
                                    String position, Class<?> actionClass) {

        var pieces = findPieces(pieceType, code);
        var foundPiece = pieces.stream()
                .filter(piece -> containsAction(piece, position, actionClass))
                .findFirst();

        return foundPiece;
    }

    static final String adapt(Piece<Color> piece, String target) {
        var source = String.valueOf(piece.getPosition());
        return String.format("%s %s", source, target);
    }

    static String formatInvalidActionMessage(String action) {
        return String.format("Invalid action format: '%s'", action);
    }

    static String formatUnknownPieceMessage(String action) {
        return String.format("Unknown source piece for action: '%s'", action);
    }

    static String prepare(String action) {
        var command = remove(remove(action, "+"), "#");
        return remove(remove(command, "!"), "?");
    }
}