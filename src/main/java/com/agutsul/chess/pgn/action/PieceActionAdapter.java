package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter.CAPTURE_CODE;
import static com.agutsul.chess.pgn.action.KingCastlingActionAdapter.CASTLING_SIDES;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.substring;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;

public final class PieceActionAdapter
        extends AbstractActionAdapter {

    private final ActionAdapter pawnMoveActionAdapter;
    private final ActionAdapter pawnCaptureActionAdapter;

    private final ActionAdapter pawnPromoteMoveActionAdapter;
    private final ActionAdapter pawnPromoteCaptureActionAdapter;

    private final ActionAdapter pieceMoveActionAdapter;
    private final ActionAdapter pieceCaptureActionAdapter;

    private final ActionAdapter kingCastlingActionAdapter;

    public PieceActionAdapter(Board board, Color color) {
        this(board, color,
                new PawnMoveActionAdapter(board, color),
                new PawnCaptureActionAdapter(board, color),
                new PawnPromoteMoveActionAdapter(board, color),
                new PawnPromoteCaptureActionAdapter(board, color),
                new PieceMoveActionAdapter(board, color),
                new PieceCaptureActionAdapter(board, color),
                new KingCastlingActionAdapter(board, color)
        );
    }

    PieceActionAdapter(Board board, Color color,
                       PawnMoveActionAdapter pawnMoveActionAdapter,
                       PawnCaptureActionAdapter pawnCaptureActionAdapter,
                       PawnPromoteMoveActionAdapter pawnPromoteMoveActionAdapter,
                       PawnPromoteCaptureActionAdapter pawnPromoteCaptureActionAdapter,
                       PieceMoveActionAdapter pieceMoveActionAdapter,
                       PieceCaptureActionAdapter pieceCaptureActionAdapter,
                       KingCastlingActionAdapter kingCastlingActionAdapter) {

        super(board, color);

        this.pawnMoveActionAdapter = pawnMoveActionAdapter;
        this.pawnCaptureActionAdapter = pawnCaptureActionAdapter;

        this.pawnPromoteMoveActionAdapter = pawnPromoteMoveActionAdapter;
        this.pawnPromoteCaptureActionAdapter = pawnPromoteCaptureActionAdapter;

        this.pieceMoveActionAdapter = pieceMoveActionAdapter;
        this.pieceCaptureActionAdapter = pieceCaptureActionAdapter;

        this.kingCastlingActionAdapter = kingCastlingActionAdapter;
    }

    @Override
    public String adapt(String action) {
        var command = prepare(action);

        if (CASTLING_SIDES.containsKey(command)) {
            return kingCastlingActionAdapter.adapt(command);
        }

        switch (command.length()) {
        case 2:
            return pawnMoveActionAdapter.adapt(command);
        case 3:
            return adaptPieceMoveAction(command);
        case 4:
            return adaptPieceAction(command);
        case 5:
        case 6:
            return adaptPieceCaptureAction(command);
        default:
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }
    }

    private String adaptPieceCaptureAction(String action) {

        if (isAllUpperCase(firstSymbol(action))) {
            return pieceCaptureActionAdapter.adapt(action);
        }

        if (isAllUpperCase(lastSymbol(action))) {
            return pawnPromoteCaptureActionAdapter.adapt(action);
        }

        throw new IllegalActionException(formatInvalidActionMessage(action));
    }

    private String adaptPieceAction(String action) {

        if (!contains(action, CAPTURE_CODE)) {
            return adaptPieceMoveAction(action);
        }

        if (isAllUpperCase(firstSymbol(action))) {
            return pieceCaptureActionAdapter.adapt(action);
        }

        return pawnCaptureActionAdapter.adapt(action);
    }

    private String adaptPieceMoveAction(String action) {

        if (isAllUpperCase(firstSymbol(action))) {
            return pieceMoveActionAdapter.adapt(action);
        }

        if (isAllUpperCase(lastSymbol(action))) {
            return pawnPromoteMoveActionAdapter.adapt(action);
        }

        throw new IllegalActionException(formatInvalidActionMessage(action));
    }

    private static String firstSymbol(String action) {
        return substring(action, 0, 1);
    }

    private static String lastSymbol(String action) {
        return substring(action, action.length() - 1);
    }
}