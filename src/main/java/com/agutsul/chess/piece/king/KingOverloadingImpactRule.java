package com.agutsul.chess.piece.king;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.overloading.PieceOverloadingPositionImpactRule;

final class KingOverloadingImpactRule<COLOR extends Color,
                                      PIECE extends KingPiece<COLOR>>
        extends PieceOverloadingPositionImpactRule<COLOR,PIECE> {

    KingOverloadingImpactRule(Board board,
                              CapturePieceAlgo<COLOR,PIECE,Position> algo) {

        super(board, new KingPieceAlgoAdapter<>(board, algo));
    }
}