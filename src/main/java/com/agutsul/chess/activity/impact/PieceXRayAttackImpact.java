package com.agutsul.chess.activity.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createHiddenAttackImpact;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class PieceXRayAttackImpact<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                         TARGET extends Piece<COLOR2>>
        extends AbstractPieceXRayImpact<COLOR1,COLOR2,SOURCE,TARGET,
                                        AbstractPieceAttackImpact<COLOR1,COLOR2,SOURCE,TARGET>> {

    public PieceXRayAttackImpact(SOURCE source, TARGET target,
                                 Collection<Piece<?>> pieces, Line line) {

        super(createHiddenAttackImpact(source, target, line), pieces);
    }

    @Override
    public Line getLine() {
        return Stream.of(getSource())
                .map(AbstractPieceAttackImpact::getLine)
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(null);
    }
}