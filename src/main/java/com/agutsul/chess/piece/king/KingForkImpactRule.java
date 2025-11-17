package com.agutsul.chess.piece.king;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.PieceForkPositionImpactRule;

final class KingForkImpactRule<COLOR1 extends Color,
                               COLOR2 extends Color,
                               ATTACKER extends KingPiece<COLOR1>,
                               ATTACKED extends Piece<COLOR2>>
        extends PieceForkPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    KingForkImpactRule(Board board,
                       CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board, algo);
    }

    @Override
    protected Collection<AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createAttackImpacts(ATTACKER piece, Collection<Calculatable> next) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> board.getPiece((Position) calculated))
                .flatMap(Optional::stream)
                .filter(attackedPiece -> !Objects.equals(attackedPiece.getColor(), piece.getColor()))
                .filter(not(Piece::isKing))
                .filter(attackedPiece -> !((Protectable) attackedPiece).isProtected())
                .map(attackedPiece -> new PieceAttackImpact<>(piece, attackedPiece))
                .map(impact -> (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact)
                .collect(toList());

        return impacts;
    }
}