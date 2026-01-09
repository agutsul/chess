package com.agutsul.chess.rule.impact.fork;

import static com.agutsul.chess.rule.impact.PieceAttackImpactFactory.createAttackImpact;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.AbstractPieceAttackImpact;
import com.agutsul.chess.activity.impact.PieceForkImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

public class PieceForkPositionImpactRule<COLOR1 extends Color,
                                         COLOR2 extends Color,
                                         ATTACKER extends Piece<COLOR1> & Capturable,
                                         ATTACKED extends Piece<COLOR2>>
            extends AbstractForkImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED,
                                           PieceForkImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>> {

    private final Algo<ATTACKER,Collection<Position>> algo;

    public PieceForkPositionImpactRule(Board board,
                                       CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER piece) {
        return unmodifiableCollection(algo.calculate(piece));
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
                .map(attackedPiece -> createAttackImpact(piece, attackedPiece))
                .map(impact -> (AbstractPieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>) impact)
                .collect(toList());

        return impacts;
    }
}