package com.agutsul.chess.pgn.action;

import java.util.regex.Pattern;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.piece.Piece;

final class PawnMoveActionAdapter
        extends AbstractActionAdapter {

    private static final String PAWN_MOVE_PATTERN = "([a-h]){1}([1-8]){1}";

    PawnMoveActionAdapter(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String adapt(String action) {
        var pattern = Pattern.compile(PAWN_MOVE_PATTERN);
        var matcher = pattern.matcher(action);

        if (!matcher.matches()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var foundPiece = getMovablePiece(Piece.Type.PAWN, action);
        if (foundPiece.isEmpty()) {
            throw new IllegalActionException(formatUnknownPieceMessage(action));
        }

        return adapt(foundPiece.get(), action);
    }
}