package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

abstract class AbstractPiecePinImpactRule<COLOR1 extends Color,
                                          COLOR2 extends Color,
                                          PINNED extends Piece<COLOR1> & Pinnable,
                                          PIECE extends Piece<COLOR1>,
                                          ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                          IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends AbstractPinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,IMPACT>
        implements LineImpactRule {

    private final Algo<PINNED,Collection<Line>> algo;

    AbstractPiecePinImpactRule(Board board, Algo<PINNED,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Line> calculate(PINNED piece) {
        return algo.calculate(piece);
    }
}