package com.agutsul.chess.activity.action;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractCaptureAction<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            PIECE1 extends Piece<COLOR1> & Capturable,
                                            PIECE2 extends Piece<COLOR2>>
        extends AbstractTargetActivity<Action.Type,PIECE1,PIECE2>
        implements Action<PIECE1> {

    AbstractCaptureAction(Action.Type type, PIECE1 piece1, PIECE2 piece2) {
        super(type, piece1, piece2);
    }

    @Override
    public Position getPosition() {
        return getTarget().getPosition();
    }

    @Override
    public final String getCode() {
        return String.format("%sx%s", getSource(), createTargetLabel(getTarget()));
    }

    @Override
    public final String toString() {
        return getCode();
    }

    String createTargetLabel(Position position) {
        return String.valueOf(position);
    }

    private final String createTargetLabel(Piece<?> targetPiece) {
        return createTargetLabel(targetPiece.getPosition());
    }
}