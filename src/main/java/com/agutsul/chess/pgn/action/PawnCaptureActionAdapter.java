package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.position.Position.codeOf;
import static java.util.regex.Pattern.compile;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.UnknownPieceException;
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
        var pattern = compile(PAWN_CAPTURE_PATTERN);
        var matcher = pattern.matcher(action);

        if (!matcher.matches()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var code = matcher.group(1);
        var position = matcher.group(2);

        var foundPiece = getCapturablePiece(Piece.Type.PAWN, code, position);
        if (foundPiece.isEmpty()) {
            throw new UnknownPieceException(formatUnknownPieceMessage(action));
        }

        return adapt(foundPiece.get(), position);
    }

    @Override
    boolean containsAction(Piece<Color> piece, String position, Action.Type actionType) {
        var actions = board.getActions(piece);
        var actionExists = actions.stream()
                .filter(action -> CAPTURE_TYPES.contains(action.getType()))
                .anyMatch(action -> Objects.equals(codeOf(action.getPosition()), position));

        return actionExists;
    }
}