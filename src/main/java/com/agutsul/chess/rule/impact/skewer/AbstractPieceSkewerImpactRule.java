package com.agutsul.chess.rule.impact.skewer;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceSkewerImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

abstract class AbstractPieceSkewerImpactRule<COLOR1 extends Color,
                                             COLOR2 extends Color,
                                             ATTACKER extends Piece<COLOR1> & Capturable & Lineable,
                                             ATTACKED extends Piece<COLOR2>,
                                             DEFENDED extends Piece<COLOR2>,
                                             IMPACT extends PieceSkewerImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
        extends AbstractSkewerImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,IMPACT> {

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