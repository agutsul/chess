package com.agutsul.chess.rule.impact.hole;

import com.agutsul.chess.activity.impact.PositionRelativeHoleImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.position.Position;

final class PositionRelativeHoleImpactRule
        extends AbstractHoleImpactRule<Position,PositionRelativeHoleImpact> {

    PositionRelativeHoleImpactRule(Board board, Color color) {
        super(board, color);
    }

    @Override
    boolean impactExists(KingPiece<?> kingPiece, Position position) {
        return Math.abs(kingPiece.getPosition().x() - position.x()) > 1;
    }

    @Override
    PositionRelativeHoleImpact createImpact(Color color, Position position) {
        return new PositionRelativeHoleImpact(color, position);
    }
}