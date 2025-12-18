package com.agutsul.chess.rule.impact.overloading;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceOverloadingPositionImpactRule<COLOR extends Color,
                                                PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractOverloadingImpactRule<COLOR,PIECE> {

    private final CapturePieceAlgo<COLOR,PIECE,Position> algo;

    public PieceOverloadingPositionImpactRule(Board board,
                                              CapturePieceAlgo<COLOR,PIECE,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }
}