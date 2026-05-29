package com.agutsul.chess.rule;

import java.util.Collection;
import java.util.stream.Stream;

import com.agutsul.chess.Calculatable;
import com.agutsul.chess.Positionable;
import com.agutsul.chess.activity.Activity;
import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.piece.algo.PositionAlgo;
import com.agutsul.chess.position.Position;

public abstract class AbstractPiecePositionRule<SOURCE extends Piece<?>,
                                                RESULT extends Positionable & Activity<TYPE,?>,
                                                TYPE   extends Enum<TYPE> & Activity.Type>
        extends AbstractPieceRule<SOURCE,RESULT,TYPE>
        implements PiecePositionRule<Calculatable> {

    protected final Collection<PositionAlgo<?>> algos;

    protected AbstractPiecePositionRule(CompositeRule<SOURCE,RESULT,TYPE> rule,
                                        Collection<PositionAlgo<?>> algos) {
        super(rule);
        this.algos = algos;
    }

    @Override
    public final Collection<Calculatable> evaluate(Position position) {
        return Stream.of(algos)
                .flatMap(Collection::stream)
                .map(algo -> algo.calculate(position))
                .flatMap(Collection::stream)
                .map(result -> (Calculatable) result)
                .distinct()
                .toList();
    }
}