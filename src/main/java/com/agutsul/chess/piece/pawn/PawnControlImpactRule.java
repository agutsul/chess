package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractControlPositionImpactRule;

class PawnControlImpactRule<COLOR extends Color,
                            PAWN extends PawnPiece<COLOR>>
        extends AbstractControlPositionImpactRule<COLOR,PAWN,
                                                  PieceControlImpact<COLOR,PAWN>> {

    PawnControlImpactRule(Board board,
                          CapturePieceAlgo<COLOR,PAWN,Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceControlImpact<COLOR,PAWN> createImpact(PAWN piece,
                                                          Position position) {
        return new PieceControlImpact<>(piece, position);
    }
}