package com.agutsul.chess.activity.action;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

public final class PieceBigMoveAction<COLOR extends Color,
                                      PIECE extends PawnPiece<COLOR>>
        extends PieceMoveAction<COLOR,PIECE> {

    public PieceBigMoveAction(PIECE piece, Position position) {
        super(piece, position);
    }
}