package com.agutsul.chess.rule.impact.promote;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Movable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.activity.impact.PiecePromoteMoveImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.MovePieceAlgo;
import com.agutsul.chess.position.Position;

final class PiecePromoteMoveImpactRule<COLOR extends Color,
                                       PIECE extends Piece<COLOR> & Movable & Promotable>
        extends AbstractPromoteImpactRule<COLOR,PIECE,
                                          PiecePromoteMoveImpact<COLOR,PIECE>> {

    private final Algo<PIECE,Collection<Position>> algo;

    PiecePromoteMoveImpactRule(Board board,
                               MovePieceAlgo<COLOR,PIECE,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(PIECE piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PiecePromoteMoveImpact<COLOR,PIECE>>
            createImpacts(PIECE piece, Collection<Calculatable> next) {

        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .filter(position -> board.isEmpty(position))
                .filter(position -> Objects.equals(position.x(), piece.getPosition().x()))
                .flatMap(position -> Stream.of(PROMOTION_TYPES)
                        .flatMap(Collection::stream)
                        .map(pieceType -> new PiecePromoteMoveImpact<>(
                                piece, position, pieceType
                        ))
                )
                .toList();

        return impacts;
    }
}