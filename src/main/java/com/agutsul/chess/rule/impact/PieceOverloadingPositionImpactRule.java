package com.agutsul.chess.rule.impact;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Calculated;
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
    protected Collection<Calculated> calculate(PIECE piece) {
        return List.copyOf(algo.calculate(piece));
    }
}