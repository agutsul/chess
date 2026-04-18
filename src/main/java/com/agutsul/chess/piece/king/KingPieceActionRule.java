package com.agutsul.chess.piece.king;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.king.KingPieceAlgoProxy.Mode;
import com.agutsul.chess.rule.AbstractPieceRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.action.PieceCapturePositionActionRule;
import com.agutsul.chess.rule.action.PieceMovePositionActionRule;

public final class KingPieceActionRule<COLOR extends Color,
                                       KING  extends KingPiece<COLOR>>
        extends AbstractPieceRule<KING,Action<?>,Action.Type> {

    public KingPieceActionRule(Board board, int castlingLine) {
        this(board, new KingPieceAlgoImpl<>(board), new KingCastlingAlgo<>(board, castlingLine));
    }

    @SuppressWarnings("unchecked")
    private KingPieceActionRule(Board board,
                                KingPieceAlgo<COLOR,KING> actionAlgo,
                                KingCastlingAlgo<COLOR,KING> castlingAlgo) {

        super(new CompositeRule<>(
                new KingCastlingActionRule<>(board, castlingAlgo),
                new PieceCapturePositionActionRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, actionAlgo)),
                new PieceMovePositionActionRule<>(board, new KingPieceAlgoProxy<>(Mode.MOVE, board, actionAlgo))
            )
        );
    }
}