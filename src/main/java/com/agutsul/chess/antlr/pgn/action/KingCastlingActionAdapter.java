package com.agutsul.chess.antlr.pgn.action;

import static com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter.CASTLING_KING_SIDE_CODE;
import static com.agutsul.chess.activity.action.formatter.StandardAlgebraicActionFormatter.CASTLING_QUEEN_SIDE_CODE;

import java.util.Map;
import java.util.Objects;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.activity.action.PieceCastlingAction;
import com.agutsul.chess.activity.action.PieceMoveAction;
import com.agutsul.chess.activity.action.adapter.ActionAdapter;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.exception.IllegalActionException;
import com.agutsul.chess.exception.UnknownPieceException;

final class KingCastlingActionAdapter
        extends AbstractActionAdapter {

    static final Map<String,Castlingable.Side> CASTLING_SIDES = Map.of(
            CASTLING_KING_SIDE_CODE,  Castlingable.Side.KING,
            CASTLING_QUEEN_SIDE_CODE, Castlingable.Side.QUEEN
    );

    KingCastlingActionAdapter(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String adapt(String action) {
        var castlingSide = CASTLING_SIDES.get(action);
        if (castlingSide == null) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        var kingPiece = board.getKing(color);
        if (kingPiece.isEmpty()) {
            throw new UnknownPieceException(formatUnknownPieceMessage(action));
        }

        var king = kingPiece.get();

        var actions = board.getActions(king, Action.Type.CASTLING);
        var targetPosition = actions.stream()
                .map(castlingAction -> (PieceCastlingAction<?,?,?>) castlingAction)
                .filter(castlingAction -> Objects.equals(castlingSide, castlingAction.getSide()))
                .map(PieceCastlingAction::getSource)
                .map(moveAction -> (PieceMoveAction<?,?>) moveAction)
                .filter(kingMoveAction -> Objects.equals(king, kingMoveAction.getSource()))
                .map(PieceMoveAction::getTarget)
                .map(String::valueOf)
                .findFirst();

        if (targetPosition.isEmpty()) {
            throw new IllegalActionException(formatInvalidActionMessage(action));
        }

        return ActionAdapter.adapt(king, targetPosition.get());
    }
}