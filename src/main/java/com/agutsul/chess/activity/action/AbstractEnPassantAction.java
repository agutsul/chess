package com.agutsul.chess.activity.action;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.PawnPiece;
import com.agutsul.chess.position.Position;

public abstract class AbstractEnPassantAction<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              PAWN1 extends PawnPiece<COLOR1>,
                                              PAWN2 extends PawnPiece<COLOR2>>
        extends AbstractCaptureAction<COLOR1,COLOR2,PAWN1,PAWN2> {

    AbstractEnPassantAction(PAWN1 pawn1, PAWN2 pawn2) {
        super(Action.Type.EN_PASSANT, pawn1, pawn2);
    }

    @Override
    String createTargetLabel(Position position) {
        return String.format("%s e.p.", getPosition());
    }
}