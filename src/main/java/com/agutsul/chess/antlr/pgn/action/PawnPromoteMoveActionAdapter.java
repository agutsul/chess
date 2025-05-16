package com.agutsul.chess.antlr.pgn.action;
import static java.util.regex.Pattern.compile;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.UnknownPieceException;

final class PawnPromoteMoveActionAdapter
        extends AbstractPgnActionAdapter {

    private static final String PROMOTE_MOVE_PATTERN =
            "([a-h]{1}[1,8]{1}){1}[=]{0,1}([N,B,R,Q]){1}";

    PawnPromoteMoveActionAdapter(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String adapt(String action) {
        var pattern = compile(PROMOTE_MOVE_PATTERN);
        var matcher = pattern.matcher(action);

        if (!matcher.matches()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var position = matcher.group(1);

        var foundPiece = getPromotablePiece(null, position);
        if (foundPiece.isEmpty()) {
            throw new UnknownPieceException(formatUnknownPieceMessage(action));
        }

        return adapt(foundPiece.get(), position);
    }
}