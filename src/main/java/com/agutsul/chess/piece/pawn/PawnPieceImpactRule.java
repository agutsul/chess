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

    public PawnPieceImpactRule(Board board, int step, int initialLine,  int promotionLine) {
        this(board, promotionLine,
                new PawnMoveAlgo<>(board, step),
                new PawnBigMoveAlgo<>(board, step, initialLine),
                new PawnCaptureAlgo<>(board, step)
        );
    }

    private PawnPieceImpactRule(Board board, int promotionLine,
                                PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                PawnCaptureAlgo<COLOR,PAWN> captureAlgo) {

        this(board, promotionLine, moveAlgo, bigMoveAlgo,
                captureAlgo, new PawnEnPassantAlgo<>(board, captureAlgo)
        );
    }

    @SuppressWarnings("unchecked")
    private PawnPieceImpactRule(Board board, int promotionLine,
                                PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                                PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo) {

        super(new CompositePieceRule<>(
                new PieceCheckPositionImpactRule<>(board, captureAlgo),
                new PieceProtectPositionImpactRule<>(board, captureAlgo),
                new PieceControlPositionImpactRule<>(board, captureAlgo),
                new PawnStagnantImpactRule<>(board, promotionLine),
                new PiecePinImpactRule<>(board),
                new PieceDiscoveredAttackImpactRule<>(board),
                new PawnBlockImpactRule<>(board, moveAlgo, bigMoveAlgo),
                new PawnForkImpactRule<>(board, captureAlgo, enPassantAlgo),
                new PawnUnderminingImpactRule<>(board, captureAlgo, enPassantAlgo)
            )
        );
    }
}