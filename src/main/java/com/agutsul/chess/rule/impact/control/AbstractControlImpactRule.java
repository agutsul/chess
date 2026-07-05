package com.agutsul.chess.rule.impact.control;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.AbstractPieceImpactRule;
import com.agutsul.chess.rule.impact.ControlImpactRule;

abstract class AbstractControlImpactRule<COLOR extends Color,
                                         PIECE extends Piece<COLOR> & Capturable & Movable,
                                         IMPACT extends PieceControlImpact<COLOR,PIECE>>
        extends AbstractPieceImpactRule<COLOR,PIECE,IMPACT>
        implements ControlImpactRule<COLOR,PIECE,IMPACT> {

    private final Algo<PIECE,Collection<Position>> algo;

    AbstractControlImpactRule(Board board,
                              Algo<PIECE,Collection<Position>> algo) {

        super(board, Impact.Type.CONTROL);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }
}