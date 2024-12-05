package com.agutsul.chess.impact;

import java.util.Optional;

import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Checkable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public class PieceCheckImpact<COLOR1 extends Color,
                              COLOR2 extends Color,
                              ATTACKER extends Piece<COLOR1> & Capturable,
                              KING extends Piece<COLOR2> & Checkable>
        extends AbstractTargetImpact<ATTACKER,KING> {

    private Line attackLine;

    public PieceCheckImpact(ATTACKER attacker, KING king) {
        super(Type.CHECK, attacker, king);
    }

    public PieceCheckImpact(ATTACKER attacker, KING king, Line line) {
        this(attacker, king);
        this.attackLine = line;
    }

    public Optional<Line> getAttackLine() {
        return Optional.ofNullable(this.attackLine);
    }

    @Override
    public String getCode() {
        return String.format("%sx%s!", getSource(), getTarget());
    }

    @Override
    public Position getPosition() {
        return getSource().getPosition();
    }
}