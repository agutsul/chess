package com.agutsul.chess.action;

import com.agutsul.chess.Color;
import com.agutsul.chess.piece.Capturable;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public abstract class AbstractCaptureAction<COLOR1 extends Color,
                                            COLOR2 extends Color,
                                            PIECE1 extends Piece<COLOR1> & Capturable,
                                            PIECE2 extends Piece<COLOR2> & Capturable>
        extends AbstractTargetAction<PIECE1, PIECE2> {

    AbstractCaptureAction(Type type, PIECE1 piece1, PIECE2 piece2) {
        super(type, piece1, piece2);
    }

    @Override
    public String getCode() {
        return String.format("%sx%s", getSource(), createTargetLabel(getTarget()));
    }

    @Override
    public Position getPosition() {
        return getTarget().getPosition();
    }

    String createTargetLabel(Piece<?> targetPiece) {
        return createTargetLabel(targetPiece.getPosition());
    }

    String createTargetLabel(Position position) {
        return String.valueOf(position);
    }
}