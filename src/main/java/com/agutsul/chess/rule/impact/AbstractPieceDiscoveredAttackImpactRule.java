package com.agutsul.chess.rule.impact;

import java.util.Collection;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDiscoveredAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Line;

abstract class AbstractPieceDiscoveredAttackImpactRule<COLOR1 extends Color,
                                                       COLOR2 extends Color,
                                                       PIECE extends Piece<COLOR1>,
                                                       ATTACKER extends Piece<COLOR1> & Capturable,
                                                       ATTACKED extends Piece<COLOR2>,
                                                       IMPACT extends PieceDiscoveredAttackImpact<COLOR1,PIECE>>
        extends AbstractDiscoveredAttackImpactRule<COLOR1,COLOR2,PIECE,ATTACKER,ATTACKED,IMPACT>
        implements LineImpactRule {

    private final Algo<PIECE,Collection<Line>> algo;

    protected AbstractPieceDiscoveredAttackImpactRule(Board board,
                                                      Algo<PIECE,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Line> calculate(PIECE piece) {
        return algo.calculate(piece);
    }
}