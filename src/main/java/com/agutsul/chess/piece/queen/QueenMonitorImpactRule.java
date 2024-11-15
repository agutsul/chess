package com.agutsul.chess.piece.queen;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.QueenPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractMonitorLineImpactRule;

class QueenMonitorImpactRule<COLOR extends Color,
                             QUEEN extends QueenPiece<COLOR>>
        extends AbstractMonitorLineImpactRule<COLOR,QUEEN,PieceMonitorImpact<COLOR,QUEEN>> {

    QueenMonitorImpactRule(Board board,
                           CapturePieceAlgo<COLOR,QUEEN,Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMonitorImpact<COLOR,QUEEN> createImpact(QUEEN piece, Position position) {
        return new PieceMonitorImpact<>(piece, position);
    }
}