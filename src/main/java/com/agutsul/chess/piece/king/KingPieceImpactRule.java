package com.agutsul.chess.piece.king;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class KingPieceImpactRule
        extends AbstractPieceRule<Impact<?>> {

    public KingPieceImpactRule(Board board) {
        this(board, new KingPieceAlgo<>(board));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private KingPieceImpactRule(Board board, KingPieceAlgo algo) {
        super(new CompositePieceRule<Impact<?>>(
                new KingProtectImpactRule<>(board, algo),
                new KingMonitorImpactRule<>(board, algo)
            )
        );
    }
}