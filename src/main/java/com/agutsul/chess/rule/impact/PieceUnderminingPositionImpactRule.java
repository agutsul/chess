package com.agutsul.chess.rule.impact;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceUnderminingAttackImpact;
import com.agutsul.chess.activity.impact.PieceUnderminingImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
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
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        return List.copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER piece, Collection<Calculatable> next) {

        @SuppressWarnings("unchecked")
        Collection<PieceUnderminingImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .map(calculated -> board.getPiece((Position) calculated))
                .flatMap(Optional::stream)
                .filter(attackedPiece -> !Objects.equals(attackedPiece.getColor(), piece.getColor()))
                .filter(attackedPiece -> isPieceAttackable(attackedPiece))
                .map(attackedPiece -> new PieceUnderminingAttackImpact<>(piece, (ATTACKED) attackedPiece))
                .collect(toList());

        return impacts;
    }
}