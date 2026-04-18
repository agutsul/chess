package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.action.PieceCaptureLineActionRule;
import com.agutsul.chess.rule.action.PieceMoveLineActionRule;

public final class RookPieceActionRule<COLOR extends Color,
                                       PIECE extends RookPiece<COLOR>>
        extends AbstractPieceRule<PIECE,Action<?>,Action.Type> {

    public RookPieceActionRule(Board board, int castlingLine) {
        this(board, new RookPieceAlgo<>(board), new RookCastlingAlgo<>(board, castlingLine));
    }

    @SuppressWarnings("unchecked")
    private RookPieceActionRule(Board board,
                                RookPieceAlgo<COLOR,PIECE> actionAlgo,
                                RookCastlingAlgo<COLOR,PIECE> castlingAlgo) {

        super(new CompositeRule<>(
                new PieceCaptureLineActionRule<>(board, actionAlgo),
                new PieceMoveLineActionRule<>(board, actionAlgo),
                new RookCastlingActionRule<>(board, castlingAlgo)
            )
        );
    }
}