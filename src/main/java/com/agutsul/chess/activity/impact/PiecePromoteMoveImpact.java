package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Movable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PiecePromoteMoveImpact<COLOR extends Color,
                                          PIECE extends Piece<COLOR> & Movable & Promotable>
        extends AbstractPiecePromoteImpact<COLOR,PIECE,
                                           PieceMotionImpact<COLOR,PIECE>> {

    public PiecePromoteMoveImpact(PIECE piece, Position position, Piece.Type pieceType) {
        super(new PieceMotionImpact<>(piece, position), pieceType);
    }
}