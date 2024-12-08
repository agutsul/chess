package com.agutsul.chess.piece.rook;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class RookPieceActionRule
        extends AbstractPieceRule<Action<?>> {

    public RookPieceActionRule(Board board) {
        this(board, new RookPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private RookPieceActionRule(Board board, RookPieceAlgo<?,?> algo) {
        super(new CompositePieceRule<Action<?>>(
                new RookCastlingActionRule<>(board),
                new RookCaptureActionRule<>(board, algo),
                new RookMoveActionRule<>(board, algo)
            )
        );
    }
}