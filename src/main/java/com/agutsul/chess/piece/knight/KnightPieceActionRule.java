package com.agutsul.chess.piece.knight;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.action.PieceCapturePositionActionRule;
import com.agutsul.chess.rule.action.PieceMovePositionActionRule;

public final class KnightPieceActionRule<COLOR extends Color,
                                         PIECE extends KnightPiece<COLOR>>
        extends AbstractPieceRule<PIECE,Action<?>,Action.Type> {

    public KnightPieceActionRule(Board board) {
        this(board, new KnightPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private KnightPieceActionRule(Board board, KnightPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositeRule<>(
                new PieceCapturePositionActionRule<>(board, algo),
                new PieceMovePositionActionRule<>(board, algo)
            )
        );
    }
}