package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Fileable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceBlankFileImpact<COLOR extends Color,
                                            PIECE extends Piece<COLOR> & Capturable & Movable & Lineable & Fileable>
        extends AbstractTargetActivity<Impact.Type,PIECE,Line>
        implements PieceBlankFileImpact<COLOR,PIECE> {

    private final Mode mode;

    AbstractPieceBlankFileImpact(Mode mode, PIECE piece, Line line) {
        super(Impact.Type.BLANK_FILE, piece, line);
        this.mode = mode;
    }

    @Override
    public final Mode getMode() {
        return mode;
    }

    @Override
    public final Line getLine() {
        return getTarget();
    }

    @Override
    public final Position getPosition() {
        return getLine().getLast();
    }

    @Override
    public final String toString() {
        return String.format("%s [%s]", getSource(), getLine());
    }
}