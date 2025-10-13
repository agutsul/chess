package com.agutsul.chess.activity.impact;

import java.util.Optional;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Line;
import com.agutsul.chess.position.Position;

public class PieceProtectImpact<COLOR extends Color,
                                PIECE1 extends Piece<COLOR> & Capturable,
                                PIECE2 extends Piece<COLOR>>
        extends AbstractTargetActivity<Impact.Type,PIECE1,PIECE2>
        implements Impact<PIECE1> {

    private Line line;

    public PieceProtectImpact(PIECE1 source, PIECE2 target) {
        super(Impact.Type.PROTECT, source, target);
    }

    public PieceProtectImpact(PIECE1 source, PIECE2 target, Line line) {
        this(source, target);
        this.line = line;
    }

    public final Optional<Line> getLine() {
        return Optional.ofNullable(this.line);
    }

    @Override
    public final Position getPosition() {
        return getTarget().getPosition();
    }

    @Override
    public final String toString() {
        return String.format("%s(%s)", getSource(), getTarget());
    }
}