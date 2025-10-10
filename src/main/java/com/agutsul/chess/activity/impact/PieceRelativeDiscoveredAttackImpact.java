package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public final class PieceRelativeDiscoveredAttackImpact<COLOR1 extends Color,
                                                       COLOR2 extends Color,
                                                       PIECE extends Piece<COLOR1>,
                                                       ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                       ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                    PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    public PieceRelativeDiscoveredAttackImpact(PIECE piece, ATTACKER attacker, ATTACKED attacked, Line line) {
        super(Mode.RELATIVE, piece, new PieceAttackImpact<>(attacker, attacked, line, true));
    }

}