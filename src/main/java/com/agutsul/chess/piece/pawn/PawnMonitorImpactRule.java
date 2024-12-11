package com.agutsul.chess.piece.pawn;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractMonitorPositionImpactRule;

@Deprecated
class PawnMonitorImpactRule<COLOR extends Color,
                            PAWN extends PawnPiece<COLOR>>
        extends AbstractMonitorPositionImpactRule<COLOR,PAWN,
                                                  PieceMonitorImpact<COLOR,PAWN>> {

    PawnMonitorImpactRule(Board board,
                          CapturePieceAlgo<COLOR,PAWN,Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMonitorImpact<COLOR,PAWN> createImpact(PAWN pawn,
                                                          Position position) {
        return new PieceMonitorImpact<>(pawn, position);
    }
}