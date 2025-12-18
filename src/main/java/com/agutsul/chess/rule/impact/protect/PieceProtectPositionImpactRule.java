package com.agutsul.chess.rule.impact.protect;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

public final class PieceProtectPositionImpactRule<COLOR extends Color,
                                                  PIECE1 extends Piece<COLOR> & Capturable,
                                                  PIECE2 extends Piece<COLOR>>
        extends AbstractProtectImpactRule<COLOR,PIECE1,PIECE2,
                                          PieceProtectImpact<COLOR,PIECE1,PIECE2>> {

    private final CapturePieceAlgo<COLOR,PIECE1,Position> algo;

    public PieceProtectPositionImpactRule(Board board,
                                          CapturePieceAlgo<COLOR,PIECE1,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE1 piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceProtectImpact<COLOR,PIECE1,PIECE2>>
            createImpacts(PIECE1 piece, Collection<Calculatable> next) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> board.getPiece((Position) calculated))
                .flatMap(Optional::stream)
                .filter(protectedPiece -> Objects.equals(protectedPiece.getColor(), piece.getColor()))
                .map(protectedPiece -> new PieceProtectImpact<>(piece, (PIECE2) protectedPiece))
                .collect(toList());

        return impacts;
    }
}