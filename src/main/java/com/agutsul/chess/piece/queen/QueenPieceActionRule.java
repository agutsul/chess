package com.agutsul.chess.piece.queen;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class QueenPieceActionRule
        extends AbstractPieceRule<Action<?>> {

    public QueenPieceActionRule(Board board) {
        this(board, new QueenPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private QueenPieceActionRule(Board board, QueenPieceAlgo<?,?> algo) {
        super(new CompositePieceRule<Action<?>>(
                new QueenCaptureActionRule<>(board, algo),
                new QueenMoveActionRule<>(board, algo)
            )
        );
    }
}