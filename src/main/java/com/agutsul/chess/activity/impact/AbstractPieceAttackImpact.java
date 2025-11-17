package com.agutsul.chess.activity.impact;

import java.util.Optional;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractPieceAttackImpact<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                ATTACKER extends Piece<COLOR1> & Capturable,
                                                ATTACKED extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,ATTACKER,ATTACKED>
        implements Impact<ATTACKER> {

    private Calculatable calculated;
    private boolean hidden;

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece) {
        super(impactType, attacker, piece);
    }

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece, boolean hidden) {
        super(impactType, attacker, piece);
        this.hidden = hidden;
    }

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece, Calculatable calculated) {
        this(impactType, attacker, piece);
        this.calculated = calculated;
    }

    AbstractPieceAttackImpact(Impact.Type impactType, ATTACKER attacker, ATTACKED piece, Calculatable calculated, boolean hidden) {
        this(impactType, attacker, piece, calculated);
        this.hidden = hidden;
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(this.calculated != null && this.calculated instanceof Line
                ? (Line) this.calculated
                : null
        );
    }

    public final boolean isHidden() {
        return this.hidden;
    }

    @Override
    public final Position getPosition() {
        return this.calculated != null && this.calculated instanceof Position
                ? (Position) this.calculated
                : getTarget().getPosition();
    }
}