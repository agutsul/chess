package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.Position.codeOf;
import static org.apache.commons.lang3.StringUtils.contains;

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

    private Optional<Piece<Color>> getPiece(Piece.Type pieceType,
                                            String code,
                                            String position,
                                            Class<?> actionClass) {

        var pieces = board.getPieces(color, pieceType);
        for (var piece : pieces) {
            if (code != null && !contains(codeOf(piece.getPosition()), code)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            var actions = board.getActions(piece, (Class<? extends Action<?>>) actionClass);
            for (var action : actions) {
                var targetPosition = codeOf(action.getPosition());
                if (Objects.equals(targetPosition, position)) {
                    return Optional.of(piece);
                }
            }
        }

        return Optional.empty();
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
}