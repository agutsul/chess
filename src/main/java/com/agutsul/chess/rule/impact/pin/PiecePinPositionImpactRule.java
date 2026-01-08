package com.agutsul.chess.rule.impact.pin;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PiecePinPositionImpactRule<COLOR1 extends Color,
                                        COLOR2 extends Color,
                                        PINNED extends Piece<COLOR1> & Pinnable,
                                        PIECE  extends Piece<COLOR1>,
                                        ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                        IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends AbstractPiecePinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,IMPACT> {

    private final Algo<PINNED,Collection<Position>> algo;

    public PiecePinPositionImpactRule(Board board,
                                      Algo<PINNED,Collection<Position>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PINNED piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }
}