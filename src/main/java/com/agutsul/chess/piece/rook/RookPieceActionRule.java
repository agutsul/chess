package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class RookPieceActionRule<COLOR extends Color,PIECE extends RookPiece<COLOR>>
        extends AbstractPieceRule<Action<?>,Action.Type> {

    public RookPieceActionRule(Board board) {
        this(board, new RookPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private RookPieceActionRule(Board board, RookPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new RookCastlingActionRule<>(board),
                new RookCaptureActionRule<>(board, algo),
                new RookMoveActionRule<>(board, algo)
            )
        );
    }
}