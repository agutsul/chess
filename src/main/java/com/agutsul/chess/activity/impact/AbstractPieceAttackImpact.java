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
                                                ATTACKED extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,ATTACKED>
        implements Impact<ATTACKER> {

    private Line line;
    private boolean hidden;

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece) {
        super(impactType, attacker, piece);
    }

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece, boolean hidden) {
        super(impactType, attacker, piece);
        this.hidden = hidden;
    }

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece, Line line) {
        this(impactType, attacker, piece);
        this.line = line;
    }

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece, Line line, boolean hidden) {
        this(impactType, attacker, piece, line);
        this.hidden = hidden;
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(this.line);
    }

    public final boolean isHidden() {
        return this.hidden;
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }
}