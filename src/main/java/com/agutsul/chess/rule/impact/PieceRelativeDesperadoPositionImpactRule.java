package com.agutsul.chess.rule.impact;

import static java.util.List.copyOf;

import java.util.Collection;
import java.util.Collections;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public final class PieceRelativeDesperadoPositionImpactRule<COLOR1 extends Color,
                                                            COLOR2 extends Color,
                                                            DESPERADO extends Piece<COLOR1> & Capturable,
                                                            ATTACKER  extends Piece<COLOR2> & Capturable,
                                                            ATTACKED  extends Piece<COLOR2>>
        extends AbstractDesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                            PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> {

    private final Algo<DESPERADO,Collection<Position>> algo;

    public PieceRelativeDesperadoPositionImpactRule(Board board,
                                                    Algo<DESPERADO,Collection<Position>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(DESPERADO piece) {
        return copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
            createImpacts(DESPERADO piece, Collection<Calculatable> next) {

        // TODO: implement
        return Collections.emptyList();
    }
}