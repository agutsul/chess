package com.agutsul.chess.piece.knight;

import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractControlPositionImpactRule;

class KnightControlImpactRule<COLOR extends Color,
                              KNIGHT extends KnightPiece<COLOR>>
        extends AbstractControlPositionImpactRule<COLOR,KNIGHT,
                                                  PieceControlImpact<COLOR,KNIGHT>> {

    KnightControlImpactRule(Board board,
                            CapturePieceAlgo<COLOR,KNIGHT,Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceControlImpact<COLOR,KNIGHT> createImpact(KNIGHT piece,
                                                            Position position) {
        return new PieceControlImpact<>(piece, position);
    }
}