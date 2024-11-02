package com.agutsul.chess.piece.king;

import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.impact.PieceMonitorImpact;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractMonitorPositionImpactRule;

class KingMonitorImpactRule<COLOR extends Color,
                            KING extends KingPiece<COLOR>>
        extends AbstractMonitorPositionImpactRule<COLOR, KING,
                                                  PieceMonitorImpact<COLOR, KING>> {

    KingMonitorImpactRule(Board board, CapturePieceAlgo<COLOR, KING, Calculated> algo) {
        super(board, algo);
    }

    @Override
    protected PieceMonitorImpact<COLOR, KING> createImpact(KING piece, Position position) {
        return new PieceMonitorImpact<COLOR, KING>(piece, position);
    }
}