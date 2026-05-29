package com.agutsul.chess.piece.pawn;

import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractPiecePositionRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.action.PieceCapturePositionActionRule;

public final class PawnPieceActionRule<COLOR extends Color,
                                       PAWN  extends PawnPiece<COLOR>>
        extends AbstractPiecePositionRule<PAWN,Action<?>,Action.Type> {

    public PawnPieceActionRule(Board board, COLOR color,
                               int step, int initialLine, int promotionLine) {

        this(board, color, promotionLine,
                new PawnMoveAlgo<>(board, step),
                new PawnBigMoveAlgo<>(board, step, initialLine),
                new PawnCaptureAlgo<>(board, step)
        );
    }

    private PawnPieceActionRule(Board board, COLOR color, int promotionLine,
                                PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                PawnCaptureAlgo<COLOR,PAWN> captureAlgo) {

        this(board, moveAlgo, bigMoveAlgo, captureAlgo,
                new PawnPromoteAlgo<>(board, promotionLine, moveAlgo, captureAlgo),
                new PawnEnPassantAlgo<>(board, color, captureAlgo)
        );
    }

    private PawnPieceActionRule(Board board,
                                PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                                PawnPromoteAlgo<COLOR,PAWN> promoteAlgo,
                                PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo) {

        super(createRule(board, moveAlgo, bigMoveAlgo, captureAlgo, promoteAlgo, enPassantAlgo),
                List.of(moveAlgo, bigMoveAlgo, captureAlgo, promoteAlgo, enPassantAlgo)
        );
    }

    @SuppressWarnings("unchecked")
    private static <COLOR extends Color,PAWN extends PawnPiece<COLOR>>
            CompositeRule<PAWN,Action<?>,Action.Type> createRule(Board board,
                                                                 PawnMoveAlgo<COLOR,PAWN> moveAlgo,
                                                                 PawnBigMoveAlgo<COLOR,PAWN> bigMoveAlgo,
                                                                 PawnCaptureAlgo<COLOR,PAWN> captureAlgo,
                                                                 PawnPromoteAlgo<COLOR,PAWN> promoteAlgo,
                                                                 PawnEnPassantAlgo<COLOR,PAWN> enPassantAlgo) {

        var moveActionRule = new PawnMoveActionRule<>(board, moveAlgo);
        var captureActionRule = new PieceCapturePositionActionRule<>(board, captureAlgo);

        return new CompositeRule<>(
                new PawnEnPassantActionRule<>(board, enPassantAlgo),
                new PawnPromoteActionRule<>(board, promoteAlgo, captureActionRule),
                new PawnPromoteActionRule<>(board, promoteAlgo, moveActionRule),
                captureActionRule,
                moveActionRule,
                new PawnBigMoveActionRule<>(board, bigMoveAlgo)
        );
    }

    @Override
    public Collection<Action<?>> evaluate(PAWN piece) {
        var positionedMap = new LinkedHashMap<Position, Action<?>>();
        // make unique actions per position to return first calculated action only
        // for example when there are promotion and move for the same position
        //               promotion should be returned
        for (var result : this.compositeRule.evaluate(piece)) {
            var targetPosition = result.getPosition();
            if (nonNull(targetPosition)
                    && !positionedMap.containsKey(targetPosition)) {

                positionedMap.put(targetPosition, result);
            }
        }

        return positionedMap.values();
    }
}