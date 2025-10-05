package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public class PieceAttackImpact<COLOR1 extends Color,
                               COLOR2 extends Color,
                               ATTACKER extends Piece<COLOR1> & Capturable,
                               ATTACKED extends Piece<COLOR2>>
        extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED> {


    public PieceAttackImpact(ATTACKER attacker, ATTACKED piece) {
        this(attacker, piece, false);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED piece, boolean hidden) {
        super(Impact.Type.ATTACK, attacker, piece, hidden);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED piece, Line line) {
        this(attacker, piece, line, false);
    }

    public PieceAttackImpact(ATTACKER attacker, ATTACKED piece, Line line, boolean hidden) {
        super(Impact.Type.ATTACK, attacker, piece, line, hidden);
    }

    @Override
    public String toString() {
        return String.format("%sx%s", getSource(), getTarget());
    }
}