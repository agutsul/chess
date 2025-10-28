package com.agutsul.chess.rule.impact;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.intersection;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceControlImpact;
import com.agutsul.chess.activity.impact.PieceOverloadingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.AbstractRule;

abstract class AbstractOverloadingImpactRule<COLOR extends Color,
                                             PIECE extends Piece<COLOR> & Capturable & Movable>
        extends AbstractRule<PIECE,PieceOverloadingImpact<COLOR,PIECE>,Impact.Type>
        implements OverloadingImpactRule<COLOR,PIECE,PieceOverloadingImpact<COLOR,PIECE>> {

    AbstractOverloadingImpactRule(Board board) {
        super(board, Impact.Type.OVERLOADING);
    }

    @Override
    public final Collection<PieceOverloadingImpact<COLOR,PIECE>> evaluate(PIECE piece) {
        var next = calculate(piece);
        if (next.isEmpty()) {
            return emptyList();
        }

        return createImpacts(piece, next);
    }

    protected abstract Collection<Calculated> calculate(PIECE piece);

    protected Collection<PieceOverloadingImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculated> next) {

        var protectedPositions = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .collect(toSet());

        var attackedPositions = Stream.of(board.getPieces(piece.getColor().invert()))
                .flatMap(Collection::stream)
                .map(opponentPiece -> board.getImpacts(opponentPiece, Impact.Type.CONTROL))
                .flatMap(Collection::stream)
                .map(impact -> (PieceControlImpact<?,?>) impact)
                .map(PieceControlImpact::getPosition)
                .collect(toList());

        var overloadedPositions = intersection(attackedPositions, protectedPositions);
        if (overloadedPositions.size() <= 1) {
            return emptyList();
        }

        var impacts = Stream.of(overloadedPositions)
                .flatMap(Collection::stream)
                .map(position -> new PieceOverloadingImpact<>(piece, position))
                .collect(toList());

        return impacts;
    }
}