package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Blockadable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public class PieceBlockadeImpact<COLOR extends Color,
                                 PIECE1 extends Piece<COLOR> & Blockadable,
                                 PIECE2 extends Piece<Color>>
        extends AbstractTargetActivity<Impact.Type,PIECE1,PIECE2>
        implements Impact<PIECE1> {

    private final ImpactValueProvider valueProvider;

    public PieceBlockadeImpact(PIECE1 source, PIECE2 blocker) {
        super(Impact.Type.BLOCKADE, source, blocker);
        this.valueProvider = new ImpactValueProvider(() -> calculateValue());
    }

    @Override
    public final Integer getValue() {
        return this.valueProvider.get();
    }

    @Override
    public final String toString() {
        return String.format("%s:%s||%s", getType(), getSource(), getTarget());
    }

    @Override
    public final Position getPosition() {
        return getTarget().getPosition();
    }

    private Integer calculateValue() {
        return Math.negateExact(getSource().getDirection());
    }
}