package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.Position.codeOf;
import static org.apache.commons.lang3.StringUtils.startsWith;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece;

final class PawnCaptureActionAdapter
        extends AbstractActionAdapter {

    private static final String PAWN_CAPTURE_PATTERN =
            "([a-h]){1}[x]{1}([a-h]{1}[1-8]{1}){1}";

    private static final Set<Action.Type> CAPTURE_TYPES =
            EnumSet.of(Action.Type.CAPTURE, Action.Type.EN_PASSANT);

    PawnCaptureActionAdapter(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String adapt(String action) {
        var pattern = Pattern.compile(PAWN_CAPTURE_PATTERN);
        var matcher = pattern.matcher(action);

        if (!matcher.matches()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var code = matcher.group(1);
        var position = matcher.group(2);

        var foundPiece = getCapturablePiece(Piece.Type.PAWN, code, position);
        if (foundPiece.isEmpty()) {
            throw new IllegalActionException(formatUnknownPieceMessage(action));
        }

        return adapt(foundPiece.get(), position);
    }

    @Override
    Optional<Piece<Color>> getCapturablePiece(Piece.Type pieceType, String code, String position) {
        var pieces = board.getPieces(color, pieceType);
        for (var piece : pieces) {
            if (code != null && !startsWith(codeOf(piece.getPosition()), code)) {
                continue;
            }

            var actions = board.getActions(piece).stream()
                            .filter(action -> CAPTURE_TYPES.contains(action.getType()))
                            .toList();

            for (var action : actions) {
                var targetPosition = codeOf(action.getPosition());
                if (Objects.equals(targetPosition, position)) {
                    return Optional.of(piece);
                }
            }
        }

        return Optional.empty();
    }
}