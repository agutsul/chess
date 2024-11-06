package com.agutsul.chess.piece.knight;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.KnightPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractMonitorPositionImpactRule;

class KnightMonitorImpactRule<COLOR extends Color,
                              KNIGHT extends KnightPiece<COLOR>>
        extends AbstractMonitorPositionImpactRule<COLOR, KNIGHT, PieceMonitorImpact<COLOR, KNIGHT>> {

    KnightMonitorImpactRule(Board board, CapturePieceAlgo<COLOR, KNIGHT, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMonitorImpact<COLOR, KNIGHT> createImpact(KNIGHT piece, Position position) {
        return new PieceMonitorImpact<>(piece, position);
    }
}