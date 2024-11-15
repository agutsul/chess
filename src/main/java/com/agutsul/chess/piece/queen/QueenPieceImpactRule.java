package com.agutsul.chess.piece.queen;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class QueenPieceImpactRule
        extends AbstractPieceRule<Impact<?>> {

    public QueenPieceImpactRule(Board board) {
        this(board, new QueenPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private QueenPieceImpactRule(Board board, QueenPieceAlgo<?,?> algo) {
        super(new CompositePieceRule<Impact<?>>(
                new QueenProtectImpactRule<>(board, algo),
                new QueenMonitorImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board)
            )
        );
    }
}