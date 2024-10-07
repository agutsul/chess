package com.agutsul.chess.piece.king;

import com.agutsul.chess.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class KingPieceActionRule extends AbstractPieceRule<Action<?>> {

    public KingPieceActionRule(Board board) {
        this(board, new KingPieceAlgo<>(board));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private KingPieceActionRule(Board board, KingPieceAlgo algo) {
        super(new CompositePieceRule<Action<?>>(
                new KingCastlingActionRule<>(board),
                new KingCaptureActionRule<>(board, algo),
                new KingMoveActionRule<>(board, algo)
            )
        );
    }
}