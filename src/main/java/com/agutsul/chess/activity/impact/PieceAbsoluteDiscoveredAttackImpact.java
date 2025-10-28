package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public final class PieceAbsoluteDiscoveredAttackImpact<COLOR1 extends Color,
                                                       COLOR2 extends Color,
                                                       PIECE extends Piece<COLOR1>,
                                                       ATTACKER extends Piece<COLOR1> & Capturable,
                                                       ATTACKED extends KingPiece<COLOR2>>
        extends AbstractPieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,
                                                    PieceCheckImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    public PieceAbsoluteDiscoveredAttackImpact(PIECE piece, ATTACKER attacker, ATTACKED king, Line line) {
        super(Mode.ABSOLUTE, piece,
                new PieceCheckImpact<>(attacker, king, line, true)
        );
    }

}