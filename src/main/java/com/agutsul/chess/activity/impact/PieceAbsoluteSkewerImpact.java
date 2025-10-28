package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;

public final class PieceAbsoluteSkewerImpact<COLOR1 extends Color,
                                             COLOR2 extends Color,
                                             ATTACKER extends Piece<COLOR1> & Capturable,
                                             ATTACKED extends KingPiece<COLOR2>,
                                             DEFENDED extends Piece<COLOR2>>
        extends AbstractPieceSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,
                                          PieceCheckImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    public PieceAbsoluteSkewerImpact(ATTACKER attacker, ATTACKED king, DEFENDED defended, Line line) {
        super(Mode.ABSOLUTE,
                new PieceCheckImpact<>(attacker, king, line),
                defended
        );
    }
}