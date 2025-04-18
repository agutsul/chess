package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public abstract class AbstractControlPositionImpactRule<COLOR extends Color,
                                                        PIECE extends Piece<COLOR> & Capturable,
                                                        IMPACT extends PieceControlImpact<COLOR,PIECE>>
        extends AbstractControlImpactRule<COLOR,PIECE,IMPACT> {

    protected final CapturePieceAlgo<COLOR,PIECE,Position> algo;

    protected AbstractControlPositionImpactRule(Board board,
                                                CapturePieceAlgo<COLOR,PIECE,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(PIECE piece) {
        return algo.calculate(piece).stream().collect(toList());
    }

    @Override
    protected Collection<IMPACT> createImpacts(PIECE piece,
                                               Collection<Calculated> positions) {
        var impacts = new ArrayList<IMPACT>();
        for (var position : positions) {
            impacts.add(createImpact(piece, (Position) position));
        }

        return impacts;
    }

    protected abstract IMPACT createImpact(PIECE piece, Position position);
}