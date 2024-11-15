package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class BishopPieceActionRule
        extends AbstractPieceRule<Action<?>> {

    public BishopPieceActionRule(Board board) {
        this(board, new BishopPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private BishopPieceActionRule(Board board, BishopPieceAlgo<?,?> algo) {
        super(new CompositePieceRule<Action<?>>(
                new BishopCaptureActionRule<>(board, algo),
                new BishopMoveActionRule<>(board, algo)
            )
        );
    }
}