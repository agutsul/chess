package com.agutsul.chess.piece.king;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class KingPieceActionRule<COLOR extends Color,
                                       PIECE extends KingPiece<COLOR>>
        extends AbstractPieceRule<Action<?>,Action.Type> {

    public KingPieceActionRule(Board board) {
        this(board, new KingPieceAlgoImpl<>(board));
    }

    @SuppressWarnings("unchecked")
    private KingPieceActionRule(Board board, KingPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new KingCastlingActionRule<>(board),
                new KingCaptureActionRule<>(board, algo),
                new KingMoveActionRule<>(board, algo)
            )
        );
    }
}