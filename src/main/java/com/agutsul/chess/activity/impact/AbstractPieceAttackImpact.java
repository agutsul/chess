package com.agutsul.chess.activity.impact;

import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public abstract class AbstractPieceAttackImpact<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                ATTACKER extends Piece<COLOR1> & Capturable,
                                                PIECE extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,PIECE>
        implements Impact<ATTACKER> {

    private Line line;

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, PIECE piece) {
        super(impactType, attacker, piece);
    }

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, PIECE piece, Line line) {
        this(impactType, attacker, piece);
        this.line = line;
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(this.line);
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }
}