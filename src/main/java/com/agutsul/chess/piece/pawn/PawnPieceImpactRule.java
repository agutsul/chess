package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.Impact;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class PawnPieceImpactRule
        extends AbstractPieceRule<Impact<?>> {

    public PawnPieceImpactRule(Board board, int step) {
        this(board, new PawnCaptureAlgo<>(board, step));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private PawnPieceImpactRule(Board board, PawnCaptureAlgo algo) {
        super(new CompositePieceRule<Impact<?>>(
                new PawnCheckImpactRule<>(board, algo),
                new PawnProtectImpactRule<>(board, algo),
                new PawnMonitorImpactRule<>(board, algo),
                new PiecePinImpactRule<>(board)
            )
        );
    }
}