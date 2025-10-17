package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.piece.Piece.isKing;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.Impact;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceCheckImpact;
import com.agutsul.chess.activity.impact.PieceDeflectionAttackImpact;
import com.agutsul.chess.activity.impact.PieceDeflectionImpact;
import com.agutsul.chess.activity.impact.PieceProtectImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.KingPiece;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Calculated;
import com.agutsul.chess.position.Position;

public class PieceDeflectionPositionImpactRule<COLOR1 extends Color,
                                               COLOR2 extends Color,
                                               ATTACKER extends Piece<COLOR1> & Capturable,
                                               ATTACKED extends Piece<COLOR2>,
                                               DEFENDED extends Piece<COLOR2>>
        extends AbstractDeflectionImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED,
                                             PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>> {

    private final CapturePieceAlgo<COLOR1,ATTACKER,Position> algo;

    public PieceDeflectionPositionImpactRule(Board board,
                                             CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculated> calculate(ATTACKER piece) {
        return List.copyOf(algo.calculate(piece));
    }

    @Override
    protected Collection<PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>>
            createImpacts(ATTACKER piece, Collection<Calculated> next) {

        @SuppressWarnings("unchecked")
        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .flatMap(calculated -> Stream.of(board.getPiece((Position) calculated))
                        .flatMap(Optional::stream)
                        .filter(attackedPiece -> !Objects.equals(attackedPiece.getColor(), piece.getColor()))
                        .map(attackedPiece -> (ATTACKED) attackedPiece)
                        .flatMap(attackedPiece -> Stream.of(board.getImpacts(attackedPiece, Impact.Type.PROTECT))
                                .flatMap(Collection::stream)
                                .map(impact -> (PieceProtectImpact<?,?,?>) impact)
                                .map(PieceProtectImpact::getTarget)
                                .filter(protectedPiece -> !board.getAttackers(protectedPiece).isEmpty())
                                // protected piece should be more valuable than attacker piece
                                .filter(protectedPiece -> protectedPiece.getType().rank() > piece.getType().rank())
                                .map(protectedPiece -> new PieceDeflectionAttackImpact<>(
                                        createAttackImpact(piece, attackedPiece),
                                        (DEFENDED) protectedPiece
                                ))
                        )
                )
                .map(impact -> (PieceDeflectionImpact<COLOR1,COLOR2,ATTACKER,ATTACKED,DEFENDED>) impact)
                .collect(toList());

        return impacts;
    }

    @SuppressWarnings("unchecked")
    protected AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>
            createAttackImpact(ATTACKER predator, ATTACKED victim) {

        var attackImpact = isKing(predator)
                ? new PieceCheckImpact<>(predator, (KingPiece<COLOR2>) victim)
                : new PieceAttackImpact<>(predator, victim);

        return (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) attackImpact;
    }
}