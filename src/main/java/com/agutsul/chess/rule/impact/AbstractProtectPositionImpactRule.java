package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public abstract class AbstractProtectPositionImpactRule<COLOR extends Color,
                                                        PIECE1 extends Piece<COLOR> & Capturable,
                                                        PIECE2 extends Piece<COLOR>,
                                                        IMPACT extends PieceProtectImpact<COLOR,PIECE1,PIECE2>>
        extends AbstractProtectImpactRule<COLOR,PIECE1,PIECE2,IMPACT> {

    protected final CapturePieceAlgo<COLOR,PIECE1,Position> algo;

    protected AbstractProtectPositionImpactRule(Board board,
                                                CapturePieceAlgo<COLOR,PIECE1,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE1 piece) {
        return algo.calculate(piece).stream().collect(toList());
    }

    @Override
    protected Collection<IMPACT> createImpacts(PIECE1 piece, Collection<Calculated> next) {
        var impacts = new ArrayList<IMPACT>();
        for (var position : next) {
            var optionalPiece = board.getPiece((Position) position);
            if (optionalPiece.isEmpty()) {
                continue;
            }

            @SuppressWarnings("unchecked")
            var piece2 = (PIECE2) optionalPiece.get();
            if (Objects.equals(piece2.getColor(), piece.getColor())) {
                impacts.add(createImpact(piece, piece2));
            }
        }

        return impacts;
    }

    protected abstract IMPACT createImpact(PIECE1 piece1, PIECE2 piece2);
}