package com.agutsul.chess.rule.impact.attack;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Capturable;
import com.agutsul.chess.activity.impact.PieceAttackImpact;
import com.agutsul.chess.board.Board;
import com.agutsul.chess.color.Color;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.Algo;
import com.agutsul.chess.piece.algo.CapturePieceAlgo;
import com.agutsul.chess.position.Position;

public final class PieceAttackPositionImpactRule<COLOR1 extends Color,
                                                 COLOR2 extends Color,
                                                 ATTACKER extends Piece<COLOR1> & Capturable,
                                                 ATTACKED extends Piece<COLOR2>>
        extends AbstractAttackImpactRule<COLOR1,COLOR2,ATTACKER,ATTACKED> {

    private final Algo<ATTACKER,Collection<Position>> algo;

    public PieceAttackPositionImpactRule(Board board,
                                        CapturePieceAlgo<COLOR1,ATTACKER,Position> algo) {
        super(board);
        this.algo = algo;
    }

    @Override
    protected Collection<Calculatable> calculate(ATTACKER attacker, ATTACKED attacked) {
        var positions = algo.calculate(attacker);
        return positions.contains(attacked.getPosition())
                ? unmodifiableCollection(positions)
                : emptyList();
    }

    @Override
    protected Collection<PieceAttackImpact<COLOR1,COLOR2,ATTACKER,ATTACKED>>
            createImpacts(ATTACKER attacker, ATTACKED attacked, Collection<Calculatable> next) {

        var impacts = Stream.of(next)
                .flatMap(Collection::stream)
                .filter(position -> Objects.equals(position, attacked.getPosition()))
                .map(position -> new PieceAttackImpact<>(attacker, attacked))
                .collect(toList());

        return impacts;
    }
}