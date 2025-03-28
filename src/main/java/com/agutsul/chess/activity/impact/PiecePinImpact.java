package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public class PiecePinImpact<COLOR1 extends Color,
                            COLOR2 extends Color,
                            PIECE extends Piece<COLOR1>,
                            KING extends KingPiece<COLOR1>,
                            ATTACKER extends Piece<COLOR2> & Capturable>
        extends AbstractTargetActivity<Impact.Type,
                                       PIECE,
                                       PieceCheckImpact<COLOR2,COLOR1,ATTACKER,KING>>
        implements Impact<PIECE> {

    public PiecePinImpact(PIECE piece, KING king, ATTACKER attacker, Line line) {
        super(Impact.Type.PIN, piece, new PieceCheckImpact<>(attacker, king, line));
    }

    @Override
    public final String toString() {
        return String.format("%s{%s}", getSource(), getTarget());
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }
}