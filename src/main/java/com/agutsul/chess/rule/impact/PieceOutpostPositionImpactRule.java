package com.agutsul.chess.rule.impact;

import java.util.Collection;
import java.util.List;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.PieceOutpostImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceOutpostPositionImpactRule<COLOR extends Color,
                                            PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractOutpostImpactRule<COLOR,PIECE,
                                          PieceOutpostImpact<COLOR,PIECE>> {

    private final Algo<PIECE,Collection<Position>> algo;

    public PieceOutpostPositionImpactRule(Board board,
                                          Algo<PIECE,Collection<Position>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return List.copyOf(algo.calculate(piece));
    }
}