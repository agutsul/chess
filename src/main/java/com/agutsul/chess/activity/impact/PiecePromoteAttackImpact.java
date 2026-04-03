package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

public final class PiecePromoteAttackImpact<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            PIECE1 extends Piece<COLOR1> & Capturable & Promotable,
                                            PIECE2 extends Piece<COLOR2>>
        extends AbstractPiecePromoteImpact<COLOR1,PIECE1,
                                           AbstractPieceAttackImpact<COLOR1,COLOR2,PIECE1,PIECE2>> {

    public PiecePromoteAttackImpact(PIECE1 attacker, PIECE2 attacked, Piece.Type pieceType) {
        super(createAttackImpact(attacker, attacked), pieceType);
    }
}