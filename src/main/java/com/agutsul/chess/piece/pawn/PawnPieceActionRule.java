package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.action.PieceCapturePositionActionRule;

public final class PawnPieceActionRule<COLOR extends Color,
                                       PAWN extends PawnPiece<COLOR>>
        extends AbstractPieceRule<Action<?>,Action.Type> {

    public PawnPieceActionRule(Board board, int step, int initialLine, int promotionLine) {
        this(board, promotionLine,
                new PawnMoveAlgo<>(board, step),
                new PawnBigMoveAlgo<>(board, step, initialLine),
                new PawnCaptureAlgo<>(board, step)
        );
    }

    private PawnPieceActionRule(Board board, int promotionLine,
                                PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                PawnCaptureAlgo<COLOR,PAWN> captureAlgo) {

        this(board, moveAlgo, bigMoveAlgo, captureAlgo,
                new PawnPromoteAlgo<>(board, promotionLine, moveAlgo, captureAlgo)
        );
    }

    private PawnPieceActionRule(Board board,
                                PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                                PawnPromoteAlgo<COLOR,PAWN> promoteAlgo) {

        super(createRule(board, moveAlgo, bigMoveAlgo, captureAlgo, promoteAlgo));
    }

    @SuppressWarnings("unchecked")
    private static <COLOR extends Color,PAWN extends PawnPiece<COLOR>>
            CompositePieceRule<Action<?>,Action.Type> createRule(Board board,
                                                                 PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                                                 PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                                                 PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                                                                 PawnPromoteAlgo<COLOR,PAWN> promoteAlgo) {

        var moveActionRule = new PawnMoveActionRule<>(board, moveAlgo);
        var captureActionRule = new PieceCapturePositionActionRule<>(board, captureAlgo);

        return new CompositePieceRule<>(
                new PawnEnPassantActionRule<>(board, new PawnEnPassantAlgo<>(board, captureAlgo)),
                new PawnPromoteActionRule<>(board, promoteAlgo, captureActionRule),
                new PawnPromoteActionRule<>(board, promoteAlgo, moveActionRule),
                captureActionRule,
                moveActionRule,
                new PawnBigMoveActionRule<>(board, bigMoveAlgo)
        );
    }

    @Override
    public Collection<Action<?>> evaluate(Piece<?> piece) {
        var positionedMap = new LinkedHashMap<Position, Action<?>>();
        // make unique actions per position to return first calculated action only
        // for example when there are promotion and move for the same position
        //               promotion should be returned
        for (var result : this.compositeRule.evaluate(piece)) {
            var targetPosition = result.getPosition();
            if (targetPosition != null
                    && !positionedMap.containsKey(targetPosition)) {

                positionedMap.put(targetPosition, result);
            }
        }

        return positionedMap.values();
    }
}