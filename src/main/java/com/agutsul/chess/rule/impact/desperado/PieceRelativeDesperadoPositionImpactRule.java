package com.agutsul.chess.rule.impact.desperado;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact.Mode;
import com.agutsul.chess.activity.impact.PieceRelativeDesperadoImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.DesperadoImpactRule;

public class PieceRelativeDesperadoPositionImpactRule<COLOR1 extends Color,
                                                      COLOR2 extends Color,
                                                      DESPERADO extends Piece<COLOR1> & Capturable,
                                                      ATTACKER  extends Piece<COLOR2> & Capturable,
                                                      ATTACKED  extends Piece<COLOR2>>
        extends AbstractDesperadoPositionImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED> {

    private final DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?> exchangeRule;

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

        var desperadoImpacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> board.getPiece((Position) calculated))
                .flatMap(Optional::stream)
                .filter(foundPiece -> !Objects.equals(foundPiece.getColor(), piece.getColor()))
                .map(opponentPiece -> findProtectImpacts(opponentPiece))
                .flatMap(Collection::stream)
                .map(impact -> createImpact(Mode.RELATIVE, piece, impact))
                .collect(toList());

        var impacts = new ArrayList<PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>();
        for (var desperadoImpact : desperadoImpacts) {
            for (var exchangeImpact : exchangeImpacts) {
                Piece<?> attacker = exchangeImpact.getAttacker();

                if (!Objects.equals(attacker, desperadoImpact.getAttacked())
                        && !Objects.equals(attacker, desperadoImpact.getDesperado())) {

                    impacts.add(new PieceRelativeDesperadoImpact<>(desperadoImpact, exchangeImpact));
                }
            }
        }

        return impacts;
    }
}