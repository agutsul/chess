package com.agutsul.chess.piece.rook;

import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractControlLineImpactRule;

class RookControlImpactRule<COLOR extends Color,
                            ROOK extends RookPiece<COLOR>>
        extends AbstractControlLineImpactRule<COLOR,ROOK,
                                              PieceControlImpact<COLOR,ROOK>> {

    RookControlImpactRule(Board board,
                          CapturePieceAlgo<COLOR,ROOK,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceControlImpact<COLOR,ROOK> createImpact(ROOK piece,
                                                          Position position) {
        return new PieceControlImpact<>(piece, position);
    }
}