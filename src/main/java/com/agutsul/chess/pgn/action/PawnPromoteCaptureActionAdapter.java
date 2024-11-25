package com.agutsul.chess.pgn.action;

import static java.util.regex.Pattern.compile;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.UnknownPieceException;
import com.agutsul.chess.piece.Piece;

final class PawnPromoteCaptureActionAdapter
        extends AbstractActionAdapter {

    private static final String PROMOTE_CAPTURE_PATTERN =
            "([a-h]{1}){1}[x]{1}([a-h]{1}[1,8]{1}){1}([N,B,R,Q]){1}";

    PawnPromoteCaptureActionAdapter(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String adapt(String action) {
        var pattern = compile(PROMOTE_CAPTURE_PATTERN);
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
}