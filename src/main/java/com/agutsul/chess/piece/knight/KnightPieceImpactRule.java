package com.agutsul.chess.piece.knight;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class KnightPieceImpactRule
        extends AbstractPieceRule<Impact<?>> {

    public KnightPieceImpactRule(Board board) {
        this(board, new KnightPieceAlgo<>(board));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private KnightPieceImpactRule(Board board, KnightPieceAlgo algo) {
        super(new CompositePieceRule<Impact<?>>(
                new KnightCheckImpactRule<>(board, algo),
                new KnightProtectImpactRule<>(board, algo),
                new KnightMonitorImpactRule<>(board, algo),
                new KnightControlImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board)
            )
        );
    }
}