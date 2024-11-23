package com.agutsul.chess.pgn.action;

import java.util.regex.Pattern;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;

public final class PawnPromotionTypeAdapter
        extends AbstractActionAdapter {

    private static final String PROMOTE_PATTERN =
            "[a-h]{0,1}[x]{0,1}[a-h]{1}[1,8]{1}([N,B,R,Q]){1}";

    public PawnPromotionTypeAdapter(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String adapt(String action) {
        var pattern = Pattern.compile(PROMOTE_PATTERN);
        var matcher = pattern.matcher(action);

        if (!matcher.matches()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        // promotion piece type
        return matcher.group(1);
    }
}