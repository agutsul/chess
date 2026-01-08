package com.agutsul.chess.activity.impact;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

public final class PieceInterferenceProtectImpact<COLOR1 extends Color,
                                                  COLOR2 extends Color,
                                                  PIECE extends Piece<COLOR1> & Movable,
                                                  PROTECTOR extends Piece<COLOR2> & Capturable & Lineable,
                                                  PROTECTED extends Piece<COLOR2>>
        extends AbstractTargetActivity<Impact.Type,PIECE,Position>
        implements PieceInterferenceImpact<COLOR1,COLOR2,PIECE,PROTECTOR,PROTECTED> {

    private final PieceProtectImpact<COLOR2,PROTECTOR,PROTECTED> protectImpact;

    public PieceInterferenceProtectImpact(PIECE source, Position target,
                                          PieceProtectImpact<COLOR2,PROTECTOR,PROTECTED> impact) {

        super(Impact.Type.INTERFERENCE, source, target);
        this.protectImpact = impact;
    }

    @Override
    public Position getPosition() {
        return getTarget();
    }

    @Override
    public Line getLine() {
        return protectImpact.getLine().get();
    }

    @Override
    public PIECE getInterferencor() {
        return getSource();
    }

    @Override
    public PROTECTED getProtected() {
        return protectImpact.getTarget();
    }

    @Override
    public PROTECTOR getProtector() {
        return protectImpact.getSource();
    }
}