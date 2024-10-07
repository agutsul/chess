package com.agutsul.chess.piece.knight;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class KnightPieceActionRule extends AbstractPieceRule<Action<?>> {

    public KnightPieceActionRule(Board board) {
        this(board, new KnightPieceAlgo<>(board));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private KnightPieceActionRule(Board board, KnightPieceAlgo algo) {
        super(new CompositePieceRule<Action<?>>(
                new KnightCaptureActionRule<>(board, algo),
                new KnightMoveActionRule<>(board, algo)
            )
        );
    }
}