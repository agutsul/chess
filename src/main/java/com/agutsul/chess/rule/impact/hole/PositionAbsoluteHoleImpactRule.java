package com.agutsul.chess.rule.impact.hole;

import com.agutsul.chess.activity.impact.PositionAbsoluteHoleImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.position.Position;

final class PositionAbsoluteHoleImpactRule
        extends AbstractHoleImpactRule<Position,PositionAbsoluteHoleImpact> {

    PositionAbsoluteHoleImpactRule(Board board, Color color) {
        super(board, color);
    }

    @Override
    boolean impactExists(KingPiece<?> kingPiece, Position position) {
        var kingPosition = kingPiece.getPosition();
        return Math.abs(kingPosition.x() - position.x()) <= 1;
    }

    @Override
    PositionAbsoluteHoleImpact createImpact(Color color, Position position) {
        return new PositionAbsoluteHoleImpact(color, position);
    }
}