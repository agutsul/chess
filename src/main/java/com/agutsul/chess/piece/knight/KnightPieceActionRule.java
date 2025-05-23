package com.agutsul.chess.piece.knight;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class KnightPieceActionRule<COLOR extends Color,PIECE extends KnightPiece<COLOR>>
        extends AbstractPieceRule<Action<?>,Action.Type> {

    public KnightPieceActionRule(Board board) {
        this(board, new KnightPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private KnightPieceActionRule(Board board, KnightPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new KnightCaptureActionRule<>(board, algo),
                new KnightMoveActionRule<>(board, algo)
            )
        );
    }
}