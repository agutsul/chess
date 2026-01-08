package com.agutsul.chess.rule.impact.pin;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.Pinnable;
import com.agutsul.chess.activity.impact.PiecePinImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

public class PiecePinLineImpactRule<COLOR1 extends Color,
                                    COLOR2 extends Color,
                                    PINNED extends Piece<COLOR1> & Pinnable & Lineable,
                                    PIECE  extends Piece<COLOR1>,
                                    ATTACKER extends Piece<COLOR2> & Capturable & Lineable,
                                    IMPACT extends PiecePinImpact<COLOR1,COLOR2,PINNED,PIECE,ATTACKER>>
        extends AbstractPiecePinImpactRule<COLOR1,COLOR2,PINNED,PIECE,ATTACKER,IMPACT> {

    private final Algo<PINNED,Collection<Line>> algo;

    public PiecePinLineImpactRule(Board board,
                                  Algo<PINNED,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PINNED piece) {
        return Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(toList());
    }
}