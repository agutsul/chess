package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;

public final class PawnPieceImpactRule
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public PawnPieceImpactRule(Board board, int step, int promotionLine) {
        this(board, new PawnCaptureAlgo<>(board, step), promotionLine);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private PawnPieceImpactRule(Board board, PawnCaptureAlgo captureAlgo, int promotionLine) {
        super(new CompositePieceRule<>(
                new PawnCheckImpactRule<>(board, captureAlgo),
                new PawnProtectImpactRule<>(board, captureAlgo),
//                new PawnMonitorImpactRule<>(board, captureAlgo),
                new PawnControlImpactRule<>(board, captureAlgo),
                new PawnBlockImpactRule<>(board, promotionLine),
                new PiecePinImpactRule<>(board)
            )
        );
    }
}