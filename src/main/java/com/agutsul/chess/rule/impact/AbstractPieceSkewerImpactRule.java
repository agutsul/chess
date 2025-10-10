package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceSkewerImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

abstract class AbstractPieceSkewerImpactRule<COLOR1 extends Color,
                                             COLOR2 extends Color,
                                             ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                             ATTACKED extends Piece<COLOR2>,
                                             DEFENDED extends Piece<COLOR2>,
                                             IMPACT extends PieceSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
        extends AbstractSkewerImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,IMPACT>
        implements LineImpactRule {

    private final Algo<ATTACKER,Collection<Line>> algo;

    AbstractPieceSkewerImpactRule(Board board, Algo<ATTACKER,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Line> calculate(ATTACKER piece) {
        return algo.calculate(piece);
    }
}