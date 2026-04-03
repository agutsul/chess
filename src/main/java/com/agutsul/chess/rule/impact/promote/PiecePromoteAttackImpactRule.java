package com.agutsul.chess.rule.impact.promote;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.Promotable;
import com.agutsul.chess.activity.impact.PiecePromoteAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

final class PiecePromoteAttackImpactRule<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         ATTACKER extends Piece<COLOR1> & Capturable & Promotable,
                                         ATTACKED extends Piece<COLOR2>>
        extends AbstractPromoteImpactRule<COLOR1,ATTACKER,
                                          PiecePromoteAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    private final Algo<ATTACKER,Collection<Position>> algo;

    PiecePromoteAttackImpactRule(Board board,
                                 CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        return unmodifiableCollection(algo.calculate(piece));
    }

    @Override
    protected Collection<PiecePromoteAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER piece, Collection<Calculatable> next) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> (Position) calculated)
                .filter(position -> !Objects.equals(position.x(), piece.getPosition().x()))
                .map(position -> board.getPiece(position))
                .flatMap(Optional::stream)
                .filter(attacked -> !Objects.equals(attacked.getColor(), piece.getColor()))
                .map(attacked -> (ATTACKED) attacked)
                .flatMap(attacked -> Stream.of(PROMOTION_TYPES)
                        .flatMap(Collection::stream)
                        .map(pieceType -> new PiecePromoteAttackImpact<>(
                                piece, attacked, pieceType
                        ))
                )
                .toList();

        return impacts;
    }
}