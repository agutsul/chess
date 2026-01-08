package com.agutsul.chess.activity.impact;

import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.AbstractTargetActivity;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;

abstract class AbstractPieceDiscoveredAttackImpact<COLOR1 extends Color,
                                                   COLOR2 extends Color,
                                                   PIECE extends Piece<COLOR1>,
                                                   ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                                   ATTACKED extends Piece<COLOR2>,
                                                   IMPACT extends AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
        extends AbstractTargetActivity<Impact.Type,PIECE,IMPACT>
        implements PieceDiscoveredAttackImpact<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED> {

    private final Mode mode;

    AbstractPieceDiscoveredAttackImpact(Mode mode, PIECE piece, IMPACT discoveredAttack) {
        super(Impact.Type.ATTACK, piece, discoveredAttack);
        this.mode = mode;
    }

    @Override
    public final Mode getMode() {
        return mode;
    }

    @Override
    public final ATTACKER getAttacker() {
        return getTarget().getSource();
    }

    @Override
    public final ATTACKED getAttacked() {
        return getTarget().getTarget();
    }

    @Override
    public final PIECE getPiece() {
        return getSource();
    }

    @Override
    public final Position getPosition() {
        return getSource().getPosition();
    }

    @Override
    public final Line getLine() {
        return Stream.of(getTarget().getLine())
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(null);
    }
}