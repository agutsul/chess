package com.agutsul.chess.rule.impact;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceUnderminingAttackImpact;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public class PieceUnderminingPositionImpactRule<COLOR1 extends Color,
                                                COLOR2 extends Color,
                                                ATTACKER extends Piece<COLOR1> & Capturable,
                                                ATTACKED extends Piece<COLOR2>>
        extends AbstractUnderminingImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,
                                              PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    private final CapturePieceAlgo<COLOR1,ATTACKER,Position> algo;

    public PieceUnderminingPositionImpactRule(Board board,
                                              CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(ATTACKER piece) {
        return algo.calculate(piece).stream().collect(toList());
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
                .map(attackedPiece -> new PieceUnderminingAttackImpact<>(piece, (ATTACKED) attackedPiece))
                .collect(toList());

        return impacts;
    }
}