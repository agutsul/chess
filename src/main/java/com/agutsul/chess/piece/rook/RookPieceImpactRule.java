package com.agutsul.chess.piece.rook;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class RookPieceImpactRule
        extends AbstractPieceRule<Impact<?>> {

    public RookPieceImpactRule(Board board) {
        this(board, new RookPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private RookPieceImpactRule(Board board, RookPieceAlgo<?,?> algo) {
        super(new CompositePieceRule<Impact<?>>(
                new RookProtectImpactRule<>(board, algo),
                new RookMonitorImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board)
            )
        );
    }
}