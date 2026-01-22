package com.agutsul.chess.piece.king;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingPositionImpactRule;

final class KingOverloadingImpactRule<COLOR extends Color,
                                      PIECE extends KingPiece<COLOR>>
        extends PieceOverloadingPositionImpactRule<COLOR,PIECE> {

    KingOverloadingImpactRule(Board board,
                              KingPieceAlgo<COLOR,PIECE> algo) {

        super(board, new KingPieceAlgoProxy<>(KingPieceAlgoProxy.Mode.CAPTURE, board, algo));
    }
}