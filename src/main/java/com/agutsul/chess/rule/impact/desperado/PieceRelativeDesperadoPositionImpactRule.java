package com.agutsul.chess.rule.impact.desperado;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact.Mode;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;

public class PieceRelativeDesperadoPositionImpactRule<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      DESPERADO extends Piece<COLOR1> & Capturable,
                                                      ATTACKER  extends Piece<COLOR2> & Capturable,
                                                      ATTACKED  extends Piece<COLOR2>>
        extends AbstractDesperadoPositionImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED>
        implements RelativeDesperadoExchangeImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> {

    private final PieceRelativeDesperadoExchangeImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> exchangeRule;

    public PieceRelativeDesperadoPositionImpactRule(Board board,
                                                    Algo<DESPERADO,Collection<Position>> algo) {
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

        Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> desperadoImpacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> findProtectImpacts(piece, (Position) calculated))
                .flatMap(Collection::stream)
                .map(protectImpact -> createImpact(Mode.RELATIVE, piece, protectImpact))
                .collect(toList());

        return createRelativeImpacts(desperadoImpacts, exchangeImpacts);
    }
}