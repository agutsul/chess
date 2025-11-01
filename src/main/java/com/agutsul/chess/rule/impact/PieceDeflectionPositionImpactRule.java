package com.agutsul.chess.rule.impact;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculated;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceDeflectionImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
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
                        .map(attackedPiece -> super.createImpacts(
                                createAttackImpact(piece, (ATTACKED) attackedPiece)
                         ))
                )
                .flatMap(Collection::stream)
                .collect(toList());

        return impacts;
    }
}