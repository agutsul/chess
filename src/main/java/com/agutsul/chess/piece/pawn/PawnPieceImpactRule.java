package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.impact.PieceCheckPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceControlPositionImpactRule;
import com.agutsul.chess.rule.impact.PieceDiscoveredAttackImpactRule;
import com.agutsul.chess.rule.impact.PiecePinImpactRule;
import com.agutsul.chess.rule.impact.PieceProtectPositionImpactRule;

public final class PawnPieceImpactRule<COLOR extends Color,
                                       PAWN extends PawnPiece<COLOR>>
        extends AbstractPieceRule<Impact<?>,Impact.Type> {

    public PawnPieceImpactRule(Board board, int step, int promotionLine) {
        this(board, new PawnCaptureAlgo<>(board, step), promotionLine);
    }

    @SuppressWarnings("unchecked")
    private PawnPieceImpactRule(Board board, PawnCaptureAlgo<COLOR,PAWN> captureAlgo, int promotionLine) {
        super(new CompositePieceRule<>(
                new PieceCheckPositionImpactRule<>(board, captureAlgo),
                new PieceProtectPositionImpactRule<>(board, captureAlgo),
                new PieceControlPositionImpactRule<>(board, captureAlgo),
                new PawnStagnantImpactRule<>(board, promotionLine),
                new PiecePinImpactRule<>(board),
                new PieceDiscoveredAttackImpactRule<>(board),
                new PawnForkImpactRule<>(board, captureAlgo, new PawnEnPassantAlgo<>(board, captureAlgo))
            )
        );
    }
}