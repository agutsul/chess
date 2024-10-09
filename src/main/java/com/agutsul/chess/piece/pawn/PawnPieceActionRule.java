package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class PawnPieceActionRule
        extends AbstractPieceRule<Action<?>> {

    public PawnPieceActionRule(Board board, int step, int initialLine, int promotionLine) {
        this(board, promotionLine,
                new PawnMoveAlgo<>(board, step, initialLine),
                new PawnCaptureAlgo<>(board, step)
        );
    }

    @SuppressWarnings("rawtypes")
    private PawnPieceActionRule(Board board, int promotionLine,
                                PawnMoveAlgo moveAlgo,
                                PawnCaptureAlgo captureAlgo) {

        super(createRule(board, promotionLine, moveAlgo, captureAlgo));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static CompositePieceRule<Action<?>> createRule(Board board, int promotionLine,
                                                            PawnMoveAlgo moveAlgo,
                                                            PawnCaptureAlgo captureAlgo) {

        var promoteAlgo = new PawnPromoteAlgo<>(board, promotionLine, moveAlgo, captureAlgo);

        var moveActionRule = new PawnMoveActionRule<>(board, moveAlgo);
        var captureActionRule = new PawnCaptureActionRule<>(board, captureAlgo);

        return new CompositePieceRule<Action<?>>(
                new PawnEnPassantActionRule<>(board, new PawnEnPassantAlgo<>(board, captureAlgo)),
                new PawnPromoteActionRule<>(board, promoteAlgo, captureActionRule),
                new PawnPromoteActionRule<>(board, promoteAlgo, moveActionRule),
                captureActionRule,
                moveActionRule
        );
    }
}
