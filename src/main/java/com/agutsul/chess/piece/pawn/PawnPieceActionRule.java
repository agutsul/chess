package com.agutsul.chess.piece.pawn;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class PawnPieceActionRule
        extends AbstractPieceRule<Action<?>,Action.Type> {

    public PawnPieceActionRule(Board board, int step, int initialLine, int promotionLine) {
        this(board, promotionLine,
                new PawnMoveAlgo<>(board, step, initialLine),
                new PawnCaptureAlgo<>(board, step)
        );
    }

    private PawnPieceActionRule(Board board, int promotionLine,
                                PawnMoveAlgo<?,?> moveAlgo,
                                PawnCaptureAlgo<?,?> captureAlgo) {

        super(createRule(board, promotionLine, moveAlgo, captureAlgo));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static CompositePieceRule<Action<?>,Action.Type> createRule(Board board,
                                                                        int promotionLine,
                                                                        PawnMoveAlgo moveAlgo,
                                                                        PawnCaptureAlgo captureAlgo) {

        var promoteAlgo = new PawnPromoteAlgo<>(board, promotionLine, moveAlgo, captureAlgo);

        var moveActionRule = new PawnMoveActionRule<>(board, moveAlgo);
        var captureActionRule = new PawnCaptureActionRule<>(board, captureAlgo);

        return new CompositePieceRule<>(
                new PawnEnPassantActionRule<>(board, new PawnEnPassantAlgo<>(board, captureAlgo)),
                new PawnPromoteActionRule<>(board, promoteAlgo, captureActionRule),
                new PawnPromoteActionRule<>(board, promoteAlgo, moveActionRule),
                captureActionRule,
                moveActionRule
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