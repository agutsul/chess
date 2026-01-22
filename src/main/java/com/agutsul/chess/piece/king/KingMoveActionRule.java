package com.agutsul.chess.piece.king;

import com.agutsul.chess.activity.action.Action;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.action.PieceMovePositionActionRule;

final class KingMoveActionRule<COLOR extends Color,
                               PIECE extends KingPiece<COLOR>>
        extends PieceMovePositionActionRule<COLOR,PIECE> {

    KingMoveActionRule(Board board,
                       KingPieceAlgo<COLOR,PIECE> algo) {

        super(Action.Type.MOVE, board,
                new KingPieceAlgoProxy<>(KingPieceAlgoProxy.Mode.MOVE, board, algo)
        );
    }
}