package com.agutsul.chess.rule.impact.desperado;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Lineable;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact.Mode;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.line.Line;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;

public final class PieceRelativeDesperadoLineImpactRule<COLOR1 extends Color,
                                                        COLOR2 extends Color,
                                                        DESPERADO extends Piece<COLOR1> & Capturable & Lineable,
                                                        ATTACKER  extends Piece<COLOR2> & Capturable,
                                                        ATTACKED  extends Piece<COLOR2>>
        extends AbstractDesperadoLineImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>
        implements RelativeDesperadoExchangeImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> {

    private final PieceRelativeDesperadoExchangeImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> exchangeRule;

    public PieceRelativeDesperadoLineImpactRule(Board board,
                                                Algo<DESPERADO,Collection<Line>> algo) {
        super(board, algo);
        this.exchangeRule = new PieceRelativeDesperadoExchangeImpactRule<>(board);
    }

    @Override
    protected Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
            createImpacts(DESPERADO piece, Collection<Calculatable> next) {

        var exchangeImpacts = exchangeRule.evaluate(piece);
        if (exchangeImpacts.isEmpty()) {
            return emptyList();
        }

        var desperadoImpacts = createDesperadoImpacts(piece, next);
        return createRelativeImpacts(desperadoImpacts, exchangeImpacts);
    }

    private Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
            createDesperadoImpacts(DESPERADO piece, Collection<Calculatable> next) {

        return Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Line) calculated)
                .flatMap(attackLine -> Stream.of(findProtectImpacts(piece, attackLine.getLast()))
                        .flatMap(Collection::stream)
                        .map(protectImpact -> createImpact(Mode.RELATIVE,
                                piece, protectImpact, attackLine
                        ))
                )
                .collect(toList());
    }
}