package com.agutsul.chess.piece.queen;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositePieceRule;

public final class QueenPieceActionRule<COLOR extends Color,PIECE extends QueenPiece<COLOR>>
        extends AbstractPieceRule<Action<?>,Action.Type> {

    public QueenPieceActionRule(Board board) {
        this(board, new QueenPieceAlgo<>(board));
    }

    @SuppressWarnings("unchecked")
    private QueenPieceActionRule(Board board, QueenPieceAlgo<COLOR,PIECE> algo) {
        super(new CompositePieceRule<>(
                new QueenCaptureActionRule<>(board, algo),
                new QueenMoveActionRule<>(board, algo)
            )
        );
    }
}