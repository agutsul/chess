package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class BishopPieceImpactRule
        extends AbstractPieceRule<Impact<?>> {

    public BishopPieceImpactRule(Board board) {
        this(board, new BishopPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private BishopPieceImpactRule(Board board, BishopPieceAlgo<?,?> algo) {
        super(new CompositePieceRule<Impact<?>>(
                new BishopCheckImpactRule<>(board, algo),
                new BishopProtectImpactRule<>(board, algo),
                new BishopMonitorImpactRule<>(board, algo),
                new BishopControlImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board)
            )
        );
    }
}