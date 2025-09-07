package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;

public class PieceAttackImpact<COLOR1 extends Color,
                               COLOR2 extends Color,
                               ATTACKER extends Piece<COLOR1> & Capturable,
                               PIECE extends Piece<COLOR2>>
        extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,PIECE> {

    private boolean hidden;

    public PieceAttackImpact(ATTACKER attacker, PIECE piece) {
        this(attacker, piece, false);
    }

    public PieceAttackImpact(ATTACKER attacker, PIECE piece, boolean hidden) {
        super(Impact.Type.ATTACK, attacker, piece);
        this.hidden = hidden;
    }

    public PieceAttackImpact(ATTACKER attacker, PIECE piece, Line line) {
        this(attacker, piece, line, false);
    }

    public PieceAttackImpact(ATTACKER attacker, PIECE piece, Line line, boolean hidden) {
        super(Impact.Type.ATTACK, attacker, piece, line);
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String toString() {
        return String.format("%sx%s", getSource(), getTarget());
    }
}