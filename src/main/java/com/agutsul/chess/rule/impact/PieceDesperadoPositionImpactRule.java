package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.CompositePieceRule;
import com.agutsul.chess.rule.Rule;

public class PieceDesperadoPositionImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              DESPERADO extends Piece<COLOR1> & Capturable,
                                              ATTACKER extends Piece<COLOR2> & Capturable,
                                              ATTACKED extends Piece<COLOR2>,
                                              IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
        extends AbstractPieceDesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,IMPACT> {

    @SuppressWarnings("unchecked")
    public PieceDesperadoPositionImpactRule(Board board,
                                            Algo<DESPERADO,Collection<Position>> algo) {

        this(board, new CompositePieceRule<>(
                new PieceAbsoluteDesperadoPositionImpactRule<>(board, algo),
                new PieceRelativeDesperadoPositionImpactRule<>(board, algo)
        ));
    }

    protected PieceDesperadoPositionImpactRule(Board board,
                                               Rule<Piece<?>,Collection<IMPACT>> rule) {

        super(board, rule);
    }
}