package com.agutsul.chess.piece.bishop;

import com.agutsul.chess.Color;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.BishopPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractMonitorLineImpactRule;

class BishopMonitorImpactRule<COLOR extends Color,
                              BISHOP extends BishopPiece<COLOR>>
        extends AbstractMonitorLineImpactRule<COLOR, BISHOP, PieceMonitorImpact<COLOR, BISHOP>> {

    BishopMonitorImpactRule(Board board, CapturePieceAlgo<COLOR, BISHOP, Line> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMonitorImpact<COLOR, BISHOP> createImpact(BISHOP piece, Position position) {
        return new PieceMonitorImpact<COLOR, BISHOP>(piece, position);
    }
}