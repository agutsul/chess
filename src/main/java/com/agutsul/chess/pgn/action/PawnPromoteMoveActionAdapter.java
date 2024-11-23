package com.agutsul.chess.pgn.action;

import java.util.regex.Pattern;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece;

final class PawnPromoteMoveActionAdapter
        extends AbstractActionAdapter {

    private static final String PROMOTE_MOVE_PATTERN =
            "([a-h]{1}[1,8]{1}){1}([N,B,R,Q]){1}";

    PawnPromoteMoveActionAdapter(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String adapt(String action) {
        var pattern = Pattern.compile(PROMOTE_MOVE_PATTERN);
        var matcher = pattern.matcher(action);

        if (!matcher.matches()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var position = matcher.group(1);

        var foundPiece = getMovablePiece(Piece.Type.PAWN, position);
        if (foundPiece.isEmpty()) {
            throw new IllegalActionException(formatUnknownPieceMessage(action));
        }

        return adapt(foundPiece.get(), position);
    }
}