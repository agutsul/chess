package com.agutsul.chess.piece.king;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.rule.impact.outpost.PieceOutpostPositionImpactRule;

final class KingOutpostImpactRule<COLOR extends Color,
                                  PIECE extends KingPiece<COLOR>>
        extends PieceOutpostPositionImpactRule<COLOR,PIECE> {

    KingOutpostImpactRule(Board board,
                          KingPieceAlgo<COLOR,PIECE> algo) {

        super(board, new KingPieceAlgoProxy<>(KingPieceAlgoProxy.Mode.MOVE, board, algo));
    }
}