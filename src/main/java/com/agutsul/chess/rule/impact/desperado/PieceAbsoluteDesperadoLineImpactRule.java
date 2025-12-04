package com.agutsul.chess.rule.impact.desperado;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceAbsoluteDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact.Mode;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

public final class PieceAbsoluteDesperadoLineImpactRule<COLOR1 extends Color,
                                                        COLOR2 extends Color,
                                                        DESPERADO extends Piece<COLOR1> & Capturable,
                                                        ATTACKER extends Piece<COLOR2> & Capturable,
                                                        ATTACKED extends Piece<COLOR2>>
        extends AbstractDesperadoLineImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> {

    public PieceAbsoluteDesperadoLineImpactRule(Board board,
                                                Algo<DESPERADO,Collection<Line>> algo) {
        super(board, algo);
    }

    @Override
    protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
            createImpacts(DESPERADO piece, Collection<Calculatable> next) {

        Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(attackLine -> Stream.of(findProtectImpacts(piece, attackLine.getLast()))
                        .flatMap(Collection::stream)
                        .map(protectImpact -> createImpact(Mode.ABSOLUTE,
                                piece, protectImpact, attackLine
                        ))
                )
                .map(PieceAbsoluteDesperadoImpact::new)
                .collect(toList());

        return impacts;
    }
}