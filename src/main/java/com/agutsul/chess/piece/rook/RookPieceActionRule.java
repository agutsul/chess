package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class RookPieceActionRule
        extends AbstractPieceRule<Action<?>,Action.Type> {

    public RookPieceActionRule(Board board) {
        this(board, new RookPieceAlgo<>(board));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private RookPieceActionRule(Board board, RookPieceAlgo algo) {
        super(new CompositePieceRule<>(
                new RookCastlingActionRule<>(board),
                new RookCaptureActionRule<>(board, algo),
                new RookMoveActionRule<>(board, algo)
            )
        );
    }
}