package com.agutsul.chess.piece.king;

import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractControlPositionImpactRule;

class KingControlImpactRule<COLOR extends Color,
                            KING extends KingPiece<COLOR>>
        extends AbstractControlPositionImpactRule<COLOR,KING,
                                                  PieceControlImpact<COLOR,KING>> {

    KingControlImpactRule(Board board,
                          CapturePieceAlgo<COLOR,KING,Position> algo) {
        super(board, algo);
    }

    @Override
    protected PieceControlImpact<COLOR,KING> createImpact(KING piece,
                                                          Position position) {
        return new PieceControlImpact<>(piece, position);
    }
}