package com.agutsul.chess.activity.impact;

import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Checkable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public class PieceCheckImpact<COLOR1 extends Color,
                              COLOR2 extends Color,
                              ATTACKER extends Piece<COLOR1> & Capturable,
                              KING extends Piece<COLOR2> & Checkable>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,KING>
        implements Impact<ATTACKER> {

    private Line line;

    public PieceCheckImpact(ATTACKER attacker, KING king) {
        super(Impact.Type.CHECK, attacker, king);
    }

    public PieceCheckImpact(ATTACKER attacker, KING king, Line line) {
        this(attacker, king);
        this.line = line;
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(this.line);
    }

    @Override
    public final String toString() {
        return String.format("%sx%s!", getSource(), getTarget());
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }
}