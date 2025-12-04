package com.agutsul.chess.rule.impact.desperado;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceRelativeDesperadoImpact;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;

interface RelativeDesperadoExchangeImpactRule<COLOR1 extends Color,
                                              COLOR2 extends Color,
                                              DESPERADO extends Piece<COLOR1> & Capturable,
                                              ATTACKER  extends Piece<COLOR2> & Capturable,
                                              ATTACKED  extends Piece<COLOR2>> {

    default Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
            createRelativeImpacts(Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> desperadoImpacts,
                                  Collection<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>> exchangeImpacts) {

        return Stream.of(desperadoImpacts)
                .flatMap(Collection::stream)
                .flatMap(desperadoImpact -> Stream.of(exchangeImpacts)
                        .flatMap(Collection::stream)
                        .filter(exchangeImpact -> {
                            Piece<?> attacker = exchangeImpact.getAttacker();
                            return !Objects.equals(attacker, desperadoImpact.getAttacked())
                                    && !Objects.equals(attacker, desperadoImpact.getDesperado());
                        })
                        .map(exchangeImpact -> new PieceRelativeDesperadoImpact<>(
                                desperadoImpact,
                                exchangeImpact
                        ))
                )
                .collect(toList());
    }
}