package com.agutsul.chess.rule;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.agutsul.chess.piece.Piece;
import com.agutsul.chess.position.Position;
import com.agutsul.chess.position.Positionable;

public abstract class AbstractPieceRule<P extends Positionable>
        implements Rule<Piece<?>, Collection<P>> {

    protected final Rule<Piece<?>, Collection<P>> rule;

    protected AbstractPieceRule(Rule<Piece<?>, Collection<P>> rule) {
        this.rule = rule;
    }

    @Override
    public Collection<P> evaluate(Piece<?> piece) {
        var positionedMap = new LinkedHashMap<Position, P>();
        for (P result : rule.evaluate(piece)) {
            var targetPosition = result.getPosition();
            if (targetPosition != null && !positionedMap.containsKey(targetPosition)) {
                positionedMap.put(targetPosition, result);
            }
        }

        return positionedMap.values();
    }
}