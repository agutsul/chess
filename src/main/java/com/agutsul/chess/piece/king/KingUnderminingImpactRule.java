package com.agutsul.chess.piece.king;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Protectable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceUnderminingAttackImpact;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.rule.impact.PieceUnderminingPositionImpactRule;

final class KingUnderminingImpactRule<COLOR1 extends Color,
                                      COLOR2 extends Color,
                                      ATTACKER extends KingPiece<COLOR1>,
                                      ATTACKED extends Piece<COLOR2>>
        extends PieceUnderminingPositionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    KingUnderminingImpactRule(Board board,
                              CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board, algo);
    }

    @Override
    protected Collection<PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER piece, Collection<Calculated> next) {

        @SuppressWarnings("unchecked")
        Collection<PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> board.getPiece((Position) calculated))
                .flatMap(Optional::stream)
                .filter(not(Piece::isKing))
                .filter(attackedPiece -> !Objects.equals(attackedPiece.getColor(), piece.getColor()))
                .filter(attackedPiece -> {
                    // check if attackedPiece protects any other opponent's piece
                    var protectImpacts = board.getImpacts(attackedPiece, Impact.Type.PROTECT);
                    return !protectImpacts.isEmpty();
                })
                .filter(attackedPiece -> !((Protectable) attackedPiece).isProtected())
                .map(attackedPiece -> new PieceUnderminingAttackImpact<>(piece, (ATTACKED) attackedPiece))
                .collect(toList());

        return impacts;
    }
}