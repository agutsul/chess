package com.agutsul.chess.piece.king;

import java.util.List;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.king.KingPieceAlgoProxy.Mode;
import com.agutsul.chess.rule.AbstractPiecePositionRule;
import com.agutsul.chess.rule.CompositeRule;
import com.agutsul.chess.rule.action.PieceCapturePositionActionRule;
import com.agutsul.chess.rule.action.PieceMovePositionActionRule;

public final class KingPieceActionRule<COLOR extends Color,
                                       KING  extends KingPiece<COLOR>>
        extends AbstractPiecePositionRule<KING,Action<?>,Action.Type> {

    public KingPieceActionRule(Board board, COLOR color, int castlingLine) {
        this(board, color, new KingPieceAlgoImpl<>(board),
                new KingCastlingAlgo<>(board, color, castlingLine)
        );
    }

    @SuppressWarnings("unchecked")
    private KingPieceActionRule(Board board, COLOR color,
                                KingPieceAlgoImpl<COLOR,KING> actionAlgo,
                                KingCastlingAlgo<COLOR,KING> castlingAlgo) {

        super(new CompositeRule<>(
                new KingCastlingActionRule<>(board, castlingAlgo),
                new PieceCapturePositionActionRule<>(board, new KingPieceAlgoProxy<>(Mode.CAPTURE, board, color, actionAlgo)),
                new PieceMovePositionActionRule<>(board, new KingPieceAlgoProxy<>(Mode.MOVE, board, color, actionAlgo))
            ),
            List.of(castlingAlgo,
                    new KingPieceAlgoProxy<>(Mode.CAPTURE, board, color, actionAlgo),
                    new KingPieceAlgoProxy<>(Mode.MOVE, board, color, actionAlgo)
            )
        );
    }
}