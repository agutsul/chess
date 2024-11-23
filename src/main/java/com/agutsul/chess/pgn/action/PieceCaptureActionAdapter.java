package com.agutsul.chess.pgn.action;

import java.util.regex.Pattern;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece;

final class PieceCaptureActionAdapter
        extends AbstractActionAdapter {

    private static final String CAPTURE_PATTERN =
            "([N,B,R,Q,K]){1}([a-h,1-8]){0,1}[x]{1}([a-h]{1}[1-8]{1}){1}";

    PieceCaptureActionAdapter(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String adapt(String action) {
        var pattern = Pattern.compile(CAPTURE_PATTERN);
        var matcher = pattern.matcher(action);

        if (!matcher.matches()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var pieceType = Piece.Type.codeOf(matcher.group(1));
        var code = matcher.group(2);
        var position = matcher.group(3);

        var foundPiece = getCapturablePiece(pieceType, code, position);
        if (foundPiece.isEmpty()) {
            throw new IllegalActionException(formatUnknownPieceMessage(action));
        }

        return adapt(foundPiece.get(), position);
    }
}