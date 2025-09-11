package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.action.PieceCaptureLineActionRule;
import com.agutsul.chess.rule.action.PieceMoveLineActionRule;

public final class BishopPieceActionRule<COLOR extends Color,
                                         PIECE extends BishopPiece<COLOR>>
        extends AbstractPieceRule<Action<?>,Action.Type> {

    public BishopPieceActionRule(Board board) {
        this(board, new BishopPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private BishopPieceActionRule(Board board, BishopPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new PieceCaptureLineActionRule<>(board, algo),
                new PieceMoveLineActionRule<>(board, algo)
            )
        );
    }
}