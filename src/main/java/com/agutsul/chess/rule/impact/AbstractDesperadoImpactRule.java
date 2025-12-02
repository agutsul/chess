package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceDesperadoImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.rule.AbstractRule;

// https://en.wikipedia.org/wiki/Desperado_(chess)
abstract class AbstractDesperadoImpactRule<COLOR1 extends Color,
                                           COLOR2 extends Color,
                                           DESPERADO extends Piece<COLOR1> & Capturable,
                                           ATTACKER extends Piece<COLOR2> & Capturable,
                                           ATTACKED extends Piece<COLOR2>,
                                           IMPACT extends PieceDesperadoImpact<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,?>>
        extends AbstractRule<DESPERADO,IMPACT,Impact.Type>
        implements DesperadoImpactRule<COLOR1,COLOR2,DESPERADO,ATTACKER,ATTACKED,IMPACT> {

    AbstractDesperadoImpactRule(Board board) {
        super(board, Impact.Type.DESPERADO);
    }

    @Override
    public final Collection<IMPACT> evaluate(DESPERADO piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculatable> calculate(DESPERADO piece);

    protected abstract Collection<IMPACT> createImpacts(DESPERADO piece, Collection<Calculatable> next);

    protected Collection<PieceProtectImpact<?,?,?>> findProtectImpacts(Piece<?> piece) {
        Collection<PieceProtectImpact<?,?,?>> impacts = Stream.of(board.getPieces(piece.getColor()))
                .flatMap(Collection::stream)
                .filter(foundPiece -> !Objects.equals(foundPiece, piece))
                .flatMap(foundPiece -> Stream.of(board.getImpacts(foundPiece, Impact.Type.PROTECT))
                        .flatMap(Collection::stream)
                        .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                        .filter(impact -> Objects.equals(impact.getTarget(), piece))
                )
                .collect(toList());

        return impacts;
    }
}