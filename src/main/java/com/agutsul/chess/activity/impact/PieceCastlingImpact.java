package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Castlingable;
import com.agutsul.chess.Castlingable.Side;
import com.agutsul.chess.Movable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceCastlingImpact<COLOR  extends Color,
                                 PIECE1 extends Piece<COLOR> & Movable & Castlingable,
                                 PIECE2 extends Piece<COLOR> & Movable & Castlingable>
        extends AbstractPieceCastlingImpact<COLOR,PIECE1,PIECE2,
                                            PieceMotionImpact<COLOR,PIECE1>,
                                            PieceMotionImpact<COLOR,PIECE2>> {

    public PieceCastlingImpact(Side side,
                               PIECE1 piece1, Position position1,
                               PIECE2 piece2, Position position2) {

        super(side,
                new PieceMotionImpact<>(piece1, position1),
                new PieceMotionImpact<>(piece2, position2)
        );
    }
}