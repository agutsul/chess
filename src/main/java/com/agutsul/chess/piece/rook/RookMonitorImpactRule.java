package com.agutsul.chess.piece.rook;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.RookPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractMonitorLineImpactRule;

class RookMonitorImpactRule<COLOR extends Color,
                            ROOK extends RookPiece<COLOR>>
        extends AbstractMonitorLineImpactRule<COLOR,ROOK,
                                              PieceMonitorImpact<COLOR,ROOK>> {

    RookMonitorImpactRule(Board board,
                          CapturePieceAlgo<COLOR,ROOK,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMonitorImpact<COLOR,ROOK> createImpact(ROOK piece,
                                                          Position position) {
        return new PieceMonitorImpact<>(piece, position);
    }
}