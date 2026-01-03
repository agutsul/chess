package com.agutsul.chess.activity.impact;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;

public final class PieceXRayProtectImpact<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          SOURCE extends Piece<COLOR1> & Capturable & Lineable,
                                          TARGET extends Piece<COLOR1>>
        extends AbstractPieceXRayImpact<COLOR1,COLOR2,SOURCE,TARGET,
                                        PieceProtectImpact<COLOR1,SOURCE,TARGET>> {

    public PieceXRayProtectImpact(SOURCE source, TARGET target,
                                  Piece<?> piece, Line line) {

        super(new PieceProtectImpact<>(source, target, line, true), List.of(piece));
    }

    @Override
    public Line getLine() {
        return Stream.of(getSource())
                .map(PieceProtectImpact::getLine)
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(null);
    }
}