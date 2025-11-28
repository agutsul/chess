package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoAttackImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

abstract class AbstractDesperadoLineImpactRule<COLOR1 extends Color,
                                               COLOR2 extends Color,
                                               DESPERADO extends Piece<COLOR1> & Capturable,
                                               ATTACKER extends Piece<COLOR2> & Capturable,
                                               ATTACKED extends Piece<COLOR2>>
        extends AbstractDesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,
                                            PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> {

    private final Algo<DESPERADO,Collection<Line>> algo;

    AbstractDesperadoLineImpactRule(Board board,
                                    Algo<DESPERADO,Collection<Line>> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(DESPERADO piece) {
        return Stream.of(algo.calculate(piece))
                .flatMap(Collection::stream)
                .filter(line -> !board.isEmpty(line.getLast()))
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    protected PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>
            createImpact(PieceDesperadoImpact.Mode mode, DESPERADO piece,
                         PieceProtectImpact<?,?,?> impact, Line attackLine) {

        return new PieceDesperadoAttackImpact<>(mode,
                createAttackImpact(piece, (ATTACKED) impact.getTarget(), attackLine),
                createAttackImpact((ATTACKER) impact.getSource(), piece, impact.getLine())
        );
    }
}