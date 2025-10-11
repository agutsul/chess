package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public final class PieceAbsolutePinImpact<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          PINNED extends Piece<COLOR1> & Pinnable,
                                          DEFENDED extends KingPiece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractPiecePinImpact<COLOR1,COLOR2,PINNED,DEFENDED,ATTACKER,
                                       PieceCheckImpact<COLOR2,COLOR1,ATTACKER,DEFENDED>> {

    public PieceAbsolutePinImpact(PINNED piece, DEFENDED king, ATTACKER attacker, Line line) {
        super(Mode.ABSOLUTE, piece,
                new PieceCheckImpact<>(attacker, king, line)
        );
    }

    @Override
    public ATTACKER getAttacker() {
        return getTarget().getSource();
    }

    @Override
    public DEFENDED getDefended() {
        return getTarget().getTarget();
    }

    @Override
    public Line getLine() {
        return getTarget().getLine().get();
    }
}