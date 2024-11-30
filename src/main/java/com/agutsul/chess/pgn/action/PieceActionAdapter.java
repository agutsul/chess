package com.agutsul.chess.pgn.action;

import static com.agutsul.chess.pgn.action.KingCastlingActionAdapter.CastlingSide.KING_SIDE;
import static com.agutsul.chess.pgn.action.KingCastlingActionAdapter.CastlingSide.QUEEN_SIDE;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isAllUpperCase;
import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.substring;

import java.util.Objects;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.pgn.action.KingCastlingActionAdapter.CastlingSide;

public final class PieceActionAdapter
        extends AbstractActionAdapter {

    private static final String CAPTURE = "x";

    private final PawnMoveActionAdapter pawnMoveActionAdapter;
    private final PawnCaptureActionAdapter pawnCaptureActionAdapter;

    private final PawnPromoteMoveActionAdapter pawnPromoteMoveActionAdapter;
    private final PawnPromoteCaptureActionAdapter pawnPromoteCaptureActionAdapter;

    private final PieceMoveActionAdapter pieceMoveActionAdapter;
    private final PieceCaptureActionAdapter pieceCaptureActionAdapter;

    public PieceActionAdapter(Board board, Color color) {
        this(board, color,
                new PawnMoveActionAdapter(board, color),
                new PawnCaptureActionAdapter(board, color),
                new PawnPromoteMoveActionAdapter(board, color),
                new PawnPromoteCaptureActionAdapter(board, color),
                new PieceMoveActionAdapter(board, color),
                new PieceCaptureActionAdapter(board, color)
        );
    }

    PieceActionAdapter(Board board, Color color,
                       PawnMoveActionAdapter pawnMoveActionAdapter,
                       PawnCaptureActionAdapter pawnCaptureActionAdapter,
                       PawnPromoteMoveActionAdapter pawnPromoteMoveActionAdapter,
                       PawnPromoteCaptureActionAdapter pawnPromoteCaptureActionAdapter,
                       PieceMoveActionAdapter pieceMoveActionAdapter,
                       PieceCaptureActionAdapter pieceCaptureActionAdapter) {

        super(board, color);

        this.pawnMoveActionAdapter = pawnMoveActionAdapter;
        this.pawnCaptureActionAdapter = pawnCaptureActionAdapter;

        this.pawnPromoteMoveActionAdapter = pawnPromoteMoveActionAdapter;
        this.pawnPromoteCaptureActionAdapter = pawnPromoteCaptureActionAdapter;

        this.pieceMoveActionAdapter = pieceMoveActionAdapter;
        this.pieceCaptureActionAdapter = pieceCaptureActionAdapter;
    }

    @Override
    public String adapt(String action) {
        var command = prepare(action);

        if (Objects.equals(KING_SIDE.code(), command)) {
            return adaptCastlingAction(command, KING_SIDE);
        }

        if (Objects.equals(QUEEN_SIDE.code(), command)) {
            return adaptCastlingAction(command, QUEEN_SIDE);
        }

        switch (command.length()) {
        case 2:
            return pawnMoveActionAdapter.adapt(command);
        case 3:
            return adaptPieceMoveAction(command);
        case 4:
            return adaptPieceAction(command);
        case 5:
            return adaptPieceCaptureAction(command);
        default:
            throw new IllegalActionException(formatInvalidActionMessage(command));
        }
    }

    private String adaptCastlingAction(String action, CastlingSide castlingSide) {
        var adapter = new KingCastlingActionAdapter(board, color, castlingSide);
        return adapter.adapt(action);
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

        if (!contains(action, CAPTURE)) {
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

    private static String prepare(String action) {
        var command = remove(remove(action, "+"), "#");
        return remove(remove(command, "!"), "?");
    }

    private static String firstSymbol(String action) {
        return substring(action, 0, 1);
    }

    private static String lastSymbol(String action) {
        return substring(action, action.length() - 1);
    }
}